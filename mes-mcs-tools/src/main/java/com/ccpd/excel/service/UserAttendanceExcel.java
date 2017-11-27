package com.ccpd.excel.service;

import com.ccpd.excel.dao.UserAttendanceDao;
import com.ccpd.excel.model.Average;
import com.ccpd.excel.model.Constants;
import com.ccpd.excel.model.UserAttendance;
import com.ccpd.excel.model.UserAttnStatistics;
import com.ccpd.util.UuidTools;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by jondai on 2017/11/22.
 */
@Service
public class UserAttendanceExcel extends ExcelOperate implements ExcelProcessable{

    @Autowired
    private UserAttendanceDao userAttendanceDao;

    @Autowired
    @PersistenceContext
    EntityManager entityManager;


    /**
     * 描述 根据excel文件路径解析并保存数据
     * @param path
     * @author daipengwei
     * @date 2017/11/23 上午9:16
     */
    public void readAndSaveUserAttendanceFile(String path) throws Exception {
        Workbook workbook = super.read(path);
        process(workbook);
    }



    /**
     * 根据部门查询统计所有的考勤数据
     * @param dept
     * @throws Exception
     */
    public List<UserAttnStatistics> AnalysisUserAtteByDept(String dept) throws Exception {
        Set<String> manufacturers = getManufacturerByDeptName(dept);

        //计算出部门下的所有厂商的出勤，并且过滤掉总工时为0的厂商
        List<UserAttnStatistics> userAttnStatisticss = new ArrayList<>();
        for (String manufacturer : manufacturers) {
            UserAttnStatistics userAttnStatistics = AnalysisUserAtteByManufacturerandDept(manufacturer, dept);
            userAttnStatisticss.add(userAttnStatistics);
        }

        List<UserAttnStatistics> userAttnStatisWorkHours = new ArrayList<>();
        for (UserAttnStatistics userAttnStatistics : userAttnStatisticss) {
            double totalWorkingHours = 0.0;
            for (Average average : userAttnStatistics.getAverages()) {
                totalWorkingHours += average.getWorkingHours();
            }


            userAttnStatistics.setTotalHours(totalWorkingHours);
            //过滤总工时小于等于0.0的厂商
//            if(totalWorkingHours > 0.0){
//                userAttnStatisWorkHours.add(userAttnStatistics);
//            }
            userAttnStatisWorkHours.add(userAttnStatistics);
        }

        //TODO 按日期排序
        userAttnStatisWorkHours.sort((u1, u2) -> u2.getTotalHours().compareTo(u1.getTotalHours()));

        return userAttnStatisWorkHours;
    }

    /**
     * 获取所有厂商名称
     * @param dept
     * @return
     */
    public Set<String> getManufacturerByDeptName(String dept){
        List<UserAttendance> byDeptName = userAttendanceDao.findByDeptName(dept);
        Set<String> manufacturerName = new HashSet<>();
        for (UserAttendance userAttendance : byDeptName) {
            manufacturerName.add(userAttendance.getManufacturer());
        }
        return manufacturerName;
    }

    /**
     * 描述 根据厂商和部门名称分析得出所有的数据结果
     * @param manufacturer
     * @param dept
     * @return
     * @author daipengwei
     * @date 2017/11/23 上午9:46
     */
    public UserAttnStatistics AnalysisUserAtteByManufacturerandDept(String manufacturer, String dept) throws Exception {

        if(StringUtils.isEmpty(manufacturer) && StringUtils.isEmpty(dept)){
            throw new Exception("部门名称或者厂商不能为空!");
        }

        List<UserAttendance> userAttendances = userAttendanceDao.findByManufacturerAndDeptName(manufacturer, dept);

        /**
         * 思路: 拿到某部门和某厂商的所有考勤数据后，
         *      1.先按日期分开
         *      2.用Set<userId>来判断上午、下午，若有重复取出来合并计算，并将日期与计算结果保存起来
         */

        Map<Date, List<UserAttendance>> userAttesByDate = separateByDate(userAttendances);

        List<Average> averages = countAveragesByDate(userAttesByDate);

        UserAttnStatistics userAttnStatistics = new UserAttnStatistics();
        userAttnStatistics.setAverages(averages);
        userAttnStatistics.setManufacturer(manufacturer);
        userAttnStatistics.setNumberOfCard("");
        userAttnStatistics.setNumberOfPerson("");
        userAttnStatistics.setPersonInCharge("");

        return userAttnStatistics;
    }


    private List<Average> countAveragesByDate(Map<Date, List<UserAttendance>> userAttesByDate) {
        List<Average> averagesByDate = new ArrayList<>();

        for (Date date : userAttesByDate.keySet()) {
            List<UserAttendance> userAttendancesByOneDate = userAttesByDate.get(date);

            Map<String,UserAttendance > userAttns = new HashMap<>();

            List<Average> allAverages = new ArrayList<>();
            userAttendancesByOneDate.forEach(userAtte -> {

                if(userAttns.containsKey(userAtte.getUserId())){
                    UserAttendance part1 = userAttns.get(userAtte.getUserId());
                    Average average = calculationAverage(part1, userAtte);
                    allAverages.add(average);

                }else{
                    //常白只有一条
                    if("常白".equals(userAtte.getTimeDuan())){
                        allAverages.add(calculationAverage(userAtte));
                    }else{
                        userAttns.put(userAtte.getUserId(),userAtte);
                    }
                }

            });


            //到此allAverage 得到一个总工时和总人数,需要得到一个平均数

            double allWorkingHours = 0.0;
            int allNumberOfOperators = 0;
            for (Average average : allAverages) {
                allWorkingHours += average.getWorkingHours();
                allNumberOfOperators += average.getNumberOfOperators();
            }

            Average average = new Average();
            double hour=getRoundingHour(allWorkingHours, (60*60*1000));
            average.setWorkingHours(hour);
            average.setNumberOfOperators(allNumberOfOperators);

            double aveWorkHours =getRoundingHour(hour,allNumberOfOperators);
            average.setAverageWorkingHours(aveWorkHours);
            average.setDate(date);

            averagesByDate.add(average);
        }

        return averagesByDate;
    }

    /**
     * 四舍五入算法
     * @param allWorkingHours
     * @param some
     * @return
     */
    private double getRoundingHour(double allWorkingHours, double some){
        if(allWorkingHours == 0.0 || some == 0){
            return 0.0;
        }else{
            double f = allWorkingHours / some;
            BigDecimal   b   =   new BigDecimal(f);
            double   f1   =   b.setScale(2,   RoundingMode.HALF_UP).doubleValue();
            return f1;
        }

    }
    /**
     * 描述 计算工时等
     * @param userAttendance
     * @return
     * @author daipengwei
     * @date 2017/11/23 上午10:21
     */
    private Average calculationAverage(UserAttendance userAttendance){
        Average average = new Average();

        SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //判断都不为空的情况，即上下班都打卡了
        if(!StringUtils.isEmpty(userAttendance.getSignTime()) &&  !StringUtils.isEmpty(userAttendance.getSignOutTime())){
            String substring = userAttendance.getAttendanceDate().toString().substring(0, 11);
            String signOutTime = substring + userAttendance.getSignOutTime() + ":00";
            String signTime =substring+ userAttendance.getSignTime() +":00";
            try {
                Date end = dfs.parse(signOutTime);
                Date begin = dfs.parse(signTime);

                double between=(end.getTime()-begin.getTime());//除以1000是为了转换成秒
                average.addWorkingHours(between);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if(average.getWorkingHours() > 0 && "常白".equals(userAttendance.getTimeDuan().trim())){
                average.numberOfOperatorAutoIncrement();
            }

        }
        return average;
    }

    /**
     * 描述 工时求和
     * @param
     * @return
     * @author daipengwei
     * @date 2017/11/23 上午10:42
     */
    private Average calculationAverage(UserAttendance part1, UserAttendance part2){
        //时段1
        Average average1 = this.calculationAverage(part1);

        //时段2
        Average average2 = this.calculationAverage(part2);


        //总和
        Average total = new Average();
        if(average1.getWorkingHours() > 0 || average2.getWorkingHours() > 0){
            total.numberOfOperatorAutoIncrement();
            total.setWorkingHours(average1.getWorkingHours() + average2.getWorkingHours());
        }
        return total;
    }

    /**
     * 描述 分隔数据按日期划分
     * @param userAttendances
     * @return
     * @author daipengwei
     * @date 2017/11/23 上午9:58
     */
    private Map<Date,List<UserAttendance>> separateByDate(List<UserAttendance> userAttendances){

//        List<Map<Date,UserAttendance>> result = new ArrayList<>();
        Map<Date,List<UserAttendance>> result = new HashMap<>();

        for (UserAttendance userAttendance : userAttendances) {
            //若已有改日期数据，取出list添加新数据
            if(result.containsKey(userAttendance.getAttendanceDate())){
                List<UserAttendance> userAttendancesByDate = result.get(userAttendance.getAttendanceDate());
                userAttendancesByDate.add(userAttendance);

                //TODO 此处应该可以不要
                result.put(userAttendance.getAttendanceDate(),userAttendancesByDate);
            }else{
                //若不存在该日期数据，存入并新建一个list保存对象
                List<UserAttendance> userAttendancesByDate = new ArrayList<>();
                userAttendancesByDate.add(userAttendance);
                result.put(userAttendance.getAttendanceDate(),userAttendancesByDate);
            }
        }

        return result;
    }

    /**
     * 描述 该方法将通过自定义编号生成部门名称，并且保存对应数据
     * @param workbook
     * @throws Exception
     * @author daipengwei
     * @date 2017/11/22 下午3:34
     */
    @Override
    public void process(Workbook workbook) throws Exception {

        if(workbook == null || workbook.getSheetAt(0) == null){
            throw new Exception("无效表!");
        }


        Sheet sheet = workbook.getSheetAt(0);

        //移除第一行的表头部分
        sheet.removeRow(sheet.getRow(0));

        AtomicReference<List<UserAttendance>> userAttendances = new AtomicReference<>(new ArrayList<>());

        System.out.println("总行数：" + sheet.getLastRowNum());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        sheet.forEach((Row row) -> {
            if(!ifRowNullOrEmpty(row)) {

                UserAttendance userAttendance = new UserAttendance();
                //自定义编号	姓名	日期	对应时段	签到时间	签到区域	签退时间	签退区域	迟到时间	早退时间	是否旷工	部门
                //必填字段人员id、人员姓名、对应时段、厂商
                userAttendance.setUserId(row.getCell(0).getStringCellValue());
                userAttendance.setUserName(row.getCell(1).getStringCellValue());
                userAttendance.setTimeDuan(row.getCell(3).getStringCellValue());
                userAttendance.setManufacturer(row.getCell(11).getStringCellValue());

                try {
                    userAttendance.setAttendanceDate(simpleDateFormat.parse(row.getCell(2).getStringCellValue()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                //非必填字段
                userAttendance.setSignTime(row.getCell(4) == null ? null : row.getCell(4).getStringCellValue());
                userAttendance.setSignAddress(row.getCell(5) == null ? null : row.getCell(5).getStringCellValue());
                userAttendance.setSignOutTime(row.getCell(6) == null ? null : row.getCell(6).getStringCellValue());
                userAttendance.setSignOutAddress(row.getCell(7) == null ? null : row.getCell(7).getStringCellValue());

                userAttendance.setLagTime(row.getCell(8) == null ? null : row.getCell(8).getStringCellValue());
                userAttendance.setLeaveEarlyTime(row.getCell(9) == null ? null : row.getCell(9).getStringCellValue());
                userAttendance.setIsAbsenteeism(row.getCell(10) == null ? null : row.getCell(10).getStringCellValue());
//                userAttendance.setManufacturer(row.getCell(11) == null ? null : row.getCell(11).getStringCellValue());

                //根据人员ID、打卡位置判断部门，填充字段
                String userId = userAttendance.getUserId();
                if (userId.startsWith("CE")) userAttendance.setDeptName("CELL");
                if (userId.startsWith("SL")) userAttendance.setDeptName("SL");
                if (userId.startsWith("FA")) userAttendance.setDeptName("FA");
                if (userId.startsWith("IT")) userAttendance.setDeptName("IT");
                if (userId.startsWith("QA")) userAttendance.setDeptName("QA");
                if (userId.startsWith("MI")) userAttendance.setDeptName("MI");
//                if (userId.startsWith("AR")) userAttendance.setDeptName("ARRAY");
                if (userId.startsWith("CF")){
                    userAttendance.setDeptName("CF");
                }

                if (userId.startsWith("AR")) {
                    /*
                      以AR开头的还需要对打卡位置进行判断
                      规则: 签到、签退有一个或两个为4A 都判为ARRAY部门
                            签到、签退都为4B  才判断为CF部
                     */
                    if ("4B".equals(userAttendance.getSignAddress().trim()) && "4B".equals(userAttendance.getSignOutAddress().trim())) {
                        userAttendance.setDeptName("CF");
                    } else {
                        userAttendance.setDeptName("ARRAY");
                    }
                }
                userAttendance.setUuid(UuidTools.getUUID());
                userAttendances.get().add(userAttendance);
            }
        });

        userAttendanceDao.save(userAttendances.get());

    }


    public void generateExcel() throws Exception {
        List<String> deptNames = new ArrayList<>();
        deptNames.add(Constants.ARRAY);

        deptNames.add(Constants.CF);
        deptNames.add(Constants.CELL);
        deptNames.add(Constants.SL);
        deptNames.add(Constants.FA);
        deptNames.add(Constants.IT);

        deptNames.add(Constants.QA);
        deptNames.add(Constants.MI);

        //取当前需要统计日期
        List<String> dates = getDateString();
//        dates.add("2017/11/20");
//        dates.add("2017/11/21");
//        dates.add("2017/11/22");

        //创建工作文档对象
        Workbook workbook = new XSSFWorkbook();

        //sheet循环
        for (String deptName : deptNames) {
            List<UserAttnStatistics> userAttnStatisticss = AnalysisUserAtteByDept(deptName);

            //创建Sheet
            Sheet sheet = workbook.createSheet(deptName);
            //添加表头
            Row row = sheet.createRow(0);
            Cell cell0 = row.createCell(0);
            cell0.setCellValue("厂商");

            Cell cell1 = row.createCell(1);
            cell1.setCellValue("工程担当");

            Cell cell2 = row.createCell(2);
            cell2.setCellValue("办卡人数");

            Cell cell3 = row.createCell(3);
            cell3.setCellValue("入场人数");

            row.createCell(4).setCellValue("");

            for (int i = 5,j =0; j < dates.size(); i++,j++) {
                row.createCell(i).setCellValue(dates.get(j));
            }

            int count = userAttnStatisticss.size() * 3;


            for (int i = 1,statisCount = 0; i <= count; i++) {

                //循环行
                Row userRow = sheet.createRow(i);
                UserAttnStatistics userAttnStatistics = userAttnStatisticss.get(statisCount);

                    if(i % 3 == 0){

                        //合并
                        sheet.addMergedRegion(new CellRangeAddress(i - 2, i, 0, 0));
                        sheet.addMergedRegion(new CellRangeAddress(i - 2, i, 1, 1));
                        sheet.addMergedRegion(new CellRangeAddress(i - 2, i, 2, 2));
                        sheet.addMergedRegion(new CellRangeAddress(i - 2, i, 3, 3));

                        //写入数据
                        sheet.getRow(i-2).createCell(0).setCellValue(userAttnStatistics.getManufacturer());
                        sheet.getRow(i-2).createCell(1).setCellValue(userAttnStatistics.getPersonInCharge());
                        sheet.getRow(i-2).createCell(2).setCellValue(userAttnStatistics.getNumberOfCard());
                        sheet.getRow(i-2).createCell(3).setCellValue(userAttnStatistics.getNumberOfPerson());

                        statisCount++;
                    }
                Map<String, Average> dateAverageMap = convertUserAttnStatisticsToMap(userAttnStatistics);
                int smallRow = i - (3 * statisCount);

                if(smallRow == 1){
//                    case 1: {
                        userRow.createCell(4).setCellValue("作业人数");
                        int cellNum = 5;
                        String dateCellValue = row.getCell(cellNum).getStringCellValue();

                        while(dateCellValue != null){
                            Average average = dateAverageMap.get(dateCellValue);
                            userRow.createCell(cellNum).setCellValue(average.getNumberOfOperators());

                            cellNum++;
                            if(row.getCell(cellNum) != null){
                                dateCellValue = row.getCell(cellNum).getStringCellValue();
                            }else{
                                break;
                            }

                        }
                    }
                if(smallRow == 2){
                        userRow.createCell(4).setCellValue("工作时数");
                        int cellNum = 5;
                        String dateCellValue = row.getCell(cellNum).getStringCellValue();

                        while(dateCellValue != null){
                            Average average = dateAverageMap.get(dateCellValue);
                            userRow.createCell(cellNum).setCellValue(average.getWorkingHours());

                            cellNum++;
                            if(row.getCell(cellNum) != null){
                                dateCellValue = row.getCell(cellNum).getStringCellValue();
                            }else{
                                break;
                            }
                        }
                    }
                if(smallRow == 0){
                        userRow.createCell(4).setCellValue("平均工作时数");
                        int cellNum = 5;
                        String dateCellValue = row.getCell(cellNum).getStringCellValue();

                        while(dateCellValue != null){
                            Average average = dateAverageMap.get(dateCellValue);
                            userRow.createCell(cellNum).setCellValue(average.getAverageWorkingHours());

                            cellNum++;
                            if(row.getCell(cellNum) != null){
                                dateCellValue = row.getCell(cellNum).getStringCellValue();
                            }else{
                                break;
                            }
                        }
                    }
                }


            //写入原始数据
            int lastRowNum = sheet.getLastRowNum() + 5;

            Row titleRow = sheet.createRow(lastRowNum);
            titleRow.createCell(0).setCellValue("自定义编号");
            titleRow.createCell(1).setCellValue("姓名");
            titleRow.createCell(2).setCellValue("日期");
            titleRow.createCell(3).setCellValue("对应时段");
            titleRow.createCell(4).setCellValue("签到时间");
            titleRow.createCell(5).setCellValue("签到区域");
            titleRow.createCell(6).setCellValue("签退时间");
            titleRow.createCell(7).setCellValue("签退区域");
            titleRow.createCell(8).setCellValue("迟到时间");
            titleRow.createCell(9).setCellValue("早退时间");
            titleRow.createCell(10).setCellValue("是否旷工");
//            titleRow.createCell(9).setCellValue("工作时间");
            titleRow.createCell(11).setCellValue("部门");
            List<UserAttendance> byDeptName = userAttendanceDao.findByDeptNameOrderByManufacturer(deptName);
            for (int i=lastRowNum+1,j=0; j<byDeptName.size(); i++,j++){
                Row original = sheet.createRow(i);
                original.createCell(0).setCellValue(byDeptName.get(j).getUserId());
                original.createCell(1).setCellValue(byDeptName.get(j).getUserName());
                original.createCell(2).setCellValue(byDeptName.get(j).getAttendanceDate());
                original.createCell(3).setCellValue(byDeptName.get(j).getTimeDuan());
                original.createCell(4).setCellValue(byDeptName.get(j).getSignTime());
                original.createCell(5).setCellValue(byDeptName.get(j).getSignAddress());
                original.createCell(6).setCellValue(byDeptName.get(j).getSignOutTime());
                original.createCell(7).setCellValue(byDeptName.get(j).getSignOutAddress());
                original.createCell(8).setCellValue(byDeptName.get(j).getLagTime());
                original.createCell(9).setCellValue(byDeptName.get(j).getLeaveEarlyTime());
                original.createCell(10).setCellValue(byDeptName.get(j).getIsAbsenteeism());
                original.createCell(11).setCellValue(byDeptName.get(j).getManufacturer());
//                original.createCell(10).setCellValue();

            }
        }

        //下载
        super.write(workbook,Constants.PATH);

    }


    private Map<String, Average> convertUserAttnStatisticsToMap(UserAttnStatistics userAttnStatistics){
        Map<String, Average> averageHashMap = new HashMap<>();
        List<Average> averages = userAttnStatistics.getAverages();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");

        for (Average average : averages) {
            String formatDate = simpleDateFormat.format(average.getDate());
            averageHashMap.put(formatDate, average);
        }

        return averageHashMap;
    }

    /**
     * 描述 判断行是否为空
     * @param row
     * @author daipengwei
     * @date 2017/11/23 下午2:58
     */
    private static boolean ifRowNullOrEmpty(Row row) {
        if (row == null || row.getLastCellNum() == 0 || row.getCell(0) == null) {
            return true;
        }
        return false;
     }


     private List<String> getDateString(){
         List<UserAttendance> byDeptName = userAttendanceDao.findByDeptNameOrderByAttendanceDate(Constants.ARRAY);
         List<String>  dates= new ArrayList<>();

         SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");

         for (UserAttendance userAttendance : byDeptName) {
             String format = simpleDateFormat.format(userAttendance.getAttendanceDate());

             if(!dates.contains(format)){
                 dates.add(format);
             }
         }


         return dates;
     }
}
