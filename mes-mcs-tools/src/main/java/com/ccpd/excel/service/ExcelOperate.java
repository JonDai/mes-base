package com.ccpd.excel.service;

import com.ccpd.excel.model.Constants;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StringUtils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jondai on 2017/11/22.
 * 操作excel文件
 */
public class ExcelOperate {

    private static Logger logger  = Logger.getLogger(ExcelOperate.class);

    public ExcelOperate() {}

    /**
     * 读取流文件
     * @param inputStream
     * @author daipengwei
     * @date 2017/11/22 上午10:47
     */
    protected Workbook read(InputStream inputStream, boolean isExcel2003) throws Exception {
        //根据excel版本选择创建方式
        Workbook workbook;
        if(isExcel2003)
            workbook = new HSSFWorkbook(inputStream);
        else
            workbook = new XSSFWorkbook(inputStream);

        return workbook;
    }

    /**
     * 描述 根据文件路径读取excel
     * @param path
     * @return Workbook
     * @author daipengwei
     * @date 2017/11/22 上午11:33
     */
    protected Workbook read(String path) throws Exception {
        if(StringUtils.isEmpty(path)) throw new Exception("路径不能为空!");
        File file = new File(path);
        checkFile(file);

        return read(new FileInputStream(file), isExcelXLS(file));
    }

    /**
     * 描述 workbook写入
     * @param workbook
     * @param path
     * @author daipengwei
     * @date 2017/11/23 下午2:57
     */
    protected void write(Workbook workbook, String path){
        FileOutputStream fileOutputStream = null;

        if(path.endsWith(".xls") || path.endsWith("xlsx")){
            try {
                fileOutputStream = new FileOutputStream(path);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }else{
            //path 是文件夹
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

            String formatDate = simpleDateFormat.format(new Date());
            String filePath = "";
            if(path.endsWith("/")){
                filePath = path +Constants.PATH_NAME+"-" + formatDate + ".xlsx" ;
            }else{
                filePath = path +"/+"+Constants.PATH_NAME+"-" + formatDate + ".xlsx" ;
            }
            File file = new File(filePath);

            try {
                fileOutputStream = new FileOutputStream(file.getPath());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        try {

            workbook.write(fileOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 描述 检查文件是否为excel文件
     * @param file
     * @author daipengwei
     * @date 2017/11/22 上午11:33
     */
    private void checkFile(File file) throws IOException {
        //判断文件是否存在
        if(null == file){
            logger.error("文件不存在！");
            throw new FileNotFoundException("文件不存在！");
        }
        //获得文件名
        String fileName = file.getAbsolutePath();
        //判断文件是否是excel文件
        if(!fileName.endsWith(Constants.OFFICE_EXCEL_XLS_POSTFIX) && !fileName.endsWith(Constants.OFFICE_EXCEL_XLSX_POSTFIX)){
            logger.error(fileName + "不是excel文件");
            throw new IOException(fileName + "不是excel文件");
        }
    }

    /**
     * 描述 判断文件类型
     * @param file
     * @return boolean
     * @author daipengwei
     * @date 2017/11/22 上午11:34
     */
    private boolean isExcelXLS(File file){
        //获得文件名
        String fileName = file.getAbsolutePath();
        if(fileName.endsWith(Constants.OFFICE_EXCEL_XLS_POSTFIX)){
            return true;
        }else{
            return false;
        }
    }
}
