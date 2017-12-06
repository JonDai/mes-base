package com.ccpd.excel.service;

import com.ccpd.excel.dao.UserAttendanceDao;
import com.ccpd.excel.model.RawData;
import com.ccpd.excel.model.UserAttendance;
import com.ccpd.util.UuidTools;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * Created by jondai on 2017/12/5.
 */
@Service
public class RawDataService extends ExcelOperate implements ExcelProcessable{

    @Autowired
    private UserAttendanceDao userAttendanceDao;

    public void readAndSaveRawDataFile(String path) throws Exception {
        Workbook workbook = super.read(path);
        process(workbook);
    }

    public void readAndSaveRawDataFile(InputStream inputStream) throws Exception {
        Workbook workbook = super.read(inputStream,false);
        process(workbook);
    }

    /**
     * 原始数据转换为标准数据
     */
    public void rawData2UserAttendance(){

    }

    @Override
    public void process(Workbook workbook) throws Exception {
        if(workbook == null || workbook.getSheetAt(0) == null){
            throw new Exception("无效表!");
        }


        Sheet sheet = workbook.getSheetAt(0);

        //删除表头
        sheet.removeRow(sheet.getRow(0));


        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        List<RawData> rawDatas = new ArrayList<>();
        Map<String, List<RawData>> byDeptMap = new HashMap<>();
        byDeptMap.put("ARRAY", new ArrayList<>());
        byDeptMap.put("CF", new ArrayList<>());
        byDeptMap.put("CELL", new ArrayList<>());
        byDeptMap.put("SL", new ArrayList<>());
        byDeptMap.put("FA", new ArrayList<>());
        byDeptMap.put("IT", new ArrayList<>());
        byDeptMap.put("QA", new ArrayList<>());
        byDeptMap.put("MI", new ArrayList<>());
        for (Row cells : sheet) {
            RawData rawData = new RawData();

            rawData.setManufacturer(cells.getCell(0).getStringCellValue());
            rawData.setUserName(cells.getCell(1).getStringCellValue());

            Date dateCellValue = dateTimeFormat.parse(cells.getCell(2).getStringCellValue());
            rawData.setSignDate(dateFormat.format(dateCellValue));
            rawData.setSignTime(timeFormat.format(dateCellValue));
            rawData.setAttendanceDate(dateCellValue);

            rawData.setUserId(cells.getCell(3).getStringCellValue());
            rawData.setSignArea(cells.getCell(4).getStringCellValue());
            
            rawDatas.add(rawData);
            
            String userId = rawData.getUserId();
            if (userId.startsWith("CE")) {
                rawData.setDeptName("CELL");
                byDeptMap.get("CELL").add(rawData);
            }
            if (userId.startsWith("SL")) {
                rawData.setDeptName("SL");
                byDeptMap.get("SL").add(rawData);
            }
            if (userId.startsWith("FA")){
                rawData.setDeptName("FA");
                byDeptMap.get("FA").add(rawData);
            }
            if (userId.startsWith("IT")){
                rawData.setDeptName("IT");
                byDeptMap.get("IT").add(rawData);
            }
            if (userId.startsWith("QA")){
                rawData.setDeptName("QA");
                byDeptMap.get("QA").add(rawData);
            }
            if (userId.startsWith("MI")){
                rawData.setDeptName("MI");
                byDeptMap.get("MI").add(rawData);
            }
            if (userId.startsWith("CF")){
                rawData.setDeptName("CF");
                byDeptMap.get("CF").add(rawData);
            }
            if (userId.startsWith("AR")){
                rawData.setDeptName("ARRAY");
                byDeptMap.get("ARRAY").add(rawData);
            }
        }

        System.out.println("数量"+ byDeptMap.size());
        //TODO 分部门处理
        CountDownLatch countDownLatch = new CountDownLatch(byDeptMap.size());
        List<UserAttendance> userAttendances = new ArrayList<>();
//        for (String key : byDeptMap.keySet()) {
//            System.out.println("部门"+ key);
//            List<RawData> byDeptRawDatas = byDeptMap.get(key);
//
//            new Thread(() -> {
                Map<String,List<RawData>> userDateMap = new LinkedHashMap<>();
                for (RawData rawData : rawDatas) {
                    String userDate = rawData.getUserId() + rawData.getSignDate();

                    if(userDateMap.containsKey(userDate)){
                        userDateMap.get(userDate).add(rawData);
                    }else{
                        List<RawData> mapRawDatas = new ArrayList<>();
                        mapRawDatas.add(rawData);
                        userDateMap.put(userDate, mapRawDatas);
                    }
                }

                
                //处理每个用户每一天的打卡情况
                for (String userDate : userDateMap.keySet()) {
                    List<RawData> userOneDay = userDateMap.get(userDate);
                    //判断用户上午、下午打卡记录
                    UserAttendance morningAtten = new UserAttendance();
                    morningAtten.setUuid(UuidTools.getUUID());
                    UserAttendance afternoonAtten = new UserAttendance();
                    afternoonAtten.setUuid(UuidTools.getUUID());

                    for (RawData rawData : userOneDay) {
                        String signTime = rawData.getSignTime();
                        String[] times = signTime.split(":");
                        int hours = Integer.valueOf(times[0]);
                        int min = Integer.valueOf(times[1]);


                        morningAtten.setManufacturer(rawData.getManufacturer());
                        morningAtten.setUserId(rawData.getUserId());
                        morningAtten.setUserName(rawData.getUserName());
                        morningAtten.setDeptName(rawData.getDeptName());
                        morningAtten.setAttendanceDate(dateFormat.parse(rawData.getSignDate()));
                        morningAtten.setTimeDuan("上午");

                        afternoonAtten.setManufacturer(rawData.getManufacturer());
                        afternoonAtten.setUserId(rawData.getUserId());
                        afternoonAtten.setUserName(rawData.getUserName());
                        afternoonAtten.setDeptName(rawData.getDeptName());
                        afternoonAtten.setAttendanceDate(dateFormat.parse(rawData.getSignDate()));
                        afternoonAtten.setTimeDuan("下午");

                        /**
                         * 上午上班：00：00——10：00
                         上午下班：10：01——12：30
                         下午上班：12：31——15：00
                         下午下班：15：01——23：59
                         */
                        if(hours < 10 || (hours == 10 && min == 0) ){
                            String morningSign = morningAtten.getSignTime();

                            morningAtten.setSignAddress(rawData.getSignArea());
                            if(StringUtils.isEmpty(morningSign)){
                                morningAtten.setSignTime(signTime);
                            }else{
                                String[] morningTimes = morningSign.split(":");
                                int morninghours = Integer.valueOf(morningTimes[0]);
                                int morningmin = Integer.valueOf(morningTimes[1]);

                                if(hours < morninghours){
                                    morningAtten.setSignTime(signTime);
                                }else if(hours == morninghours && min < morningmin){
                                    morningAtten.setSignTime(signTime);
                                }
                            }
                        }else if((hours >= 10 && hours < 12) || (hours == 12 && min <= 30)){
                            //上午签退
                            String morningSignout = morningAtten.getSignOutTime();

                            morningAtten.setSignOutAddress(rawData.getSignArea());
                            if(StringUtils.isEmpty(morningSignout)){
                                morningAtten.setSignOutTime(signTime);
                            }else{
                                String[] morningTimes = morningSignout.split(":");
                                int morninghours = Integer.valueOf(morningTimes[0]);
                                int morningmin = Integer.valueOf(morningTimes[1]);

                                if(hours < morninghours){
                                    morningAtten.setSignOutTime(signTime);
                                }else if(hours == morninghours && min < morningmin){
                                    morningAtten.setSignOutTime(signTime);
                                }
                            }
                            //下午打卡
                        }else if(hours < 15 || (hours == 15 && min == 0)){
                            //下午签到
                            String afternoonSign = afternoonAtten.getSignTime();

                            afternoonAtten.setSignAddress(rawData.getSignArea());
                            if(StringUtils.isEmpty(afternoonSign)){
                                afternoonAtten.setSignTime(signTime);

                            }else{
                                String[] afternoonTimes = afternoonSign.split(":");
                                int afternoonhours = Integer.valueOf(afternoonTimes[0]);
                                int afternoonmin = Integer.valueOf(afternoonTimes[1]);

                                if(hours < afternoonhours){
                                    afternoonAtten.setSignTime(signTime);
                                }else if(hours == afternoonhours && min < afternoonmin){
                                    afternoonAtten.setSignTime(signTime);
                                }
                            }
                        }else{
                            //下午签退
                            String afternoonSignout = afternoonAtten.getSignOutTime();

                            afternoonAtten.setSignOutAddress(rawData.getSignArea());

                            if(StringUtils.isEmpty(afternoonSignout)){
                                afternoonAtten.setSignOutTime(signTime);
                            }else{
                                String[] afternoonTimes = afternoonSignout.split(":");
                                int afternoonhours = Integer.valueOf(afternoonTimes[0]);
                                int afternoonmin = Integer.valueOf(afternoonTimes[1]);

                                if(hours < afternoonhours){
                                    afternoonAtten.setSignOutTime(signTime);
                                }else if(hours == afternoonhours && min < afternoonmin){
                                    afternoonAtten.setSignOutTime(signTime);
                                }
                        }

                    }


                }
                    userAttendances.add(morningAtten);
                    userAttendances.add(afternoonAtten);


                }

                System.out.println(Thread.currentThread().getName());
//                countDownLatch.countDown();

//            }).start();
//        }

//        System.out.println("hello");
//        countDownLatch.await();
        System.out.println("save...");

        userAttendances.forEach((user)-> {
            if(user != null){
                String userId = user.getUserId();
                if (userId.startsWith("AR")) {
                    /*
                      以AR开头的还需要对打卡位置进行判断
                      规则: 签到、签退有一个或两个为4A 都判为ARRAY部门
                            签到、签退都为4B  才判断为CF部
                     */
                    if(!StringUtils.isEmpty(user.getSignAddress()) && !StringUtils.isEmpty(user.getSignOutAddress())){
                        if ("4B".equals(user.getSignAddress().trim()) && "4B".equals(user.getSignOutAddress().trim())) {
                            user.setDeptName("CF");
                        }
                    }

                }

                userAttendanceDao.save(user);
            }else{
                System.out.println("user is null");
            }

        });


    }
}
