package com.ccpd.app;

import com.alibaba.fastjson.JSON;
import com.ccpd.excel.dao.UserAttendanceDao;
import com.ccpd.excel.model.Constants;
import com.ccpd.excel.service.UserAttendanceExcel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jondai on 2017/11/27.
 * 文件操作
 */
@RequestMapping("file")
@RestController
public class FileUploadController {

    @Autowired
    private UserAttendanceExcel userAttendanceExcel;

    @Autowired
    private UserAttendanceDao userAttendanceDao;



    @RequestMapping("upload")
    @ResponseBody
    @Transactional
    public String uploadFile(MultipartHttpServletRequest mutReq, HttpServletRequest request){

        String filePathName = mutReq.getFile("file").getOriginalFilename();

//        String fileName = filePathName.substring(filePathName.lastIndexOf("\\") + 1, filePathName.indexOf("."));

        String fileSuffix = filePathName.substring(filePathName.lastIndexOf(".") + 1, filePathName.length());

        Map<String,String> result = new HashMap<>();
        if(!Constants.OFFICE_EXCEL_XLSX_POSTFIX.equals(fileSuffix)){
            //TODO error handle

            result.put("fail", "请转为化xlsx格式!!" );

        }else{

            //将文件流拿去处理
            try {

                //删除数据库中的数据
                userAttendanceDao.deleteAllInBatch();

                //生成数据
                userAttendanceExcel.readAndSaveUserAttendanceFile(mutReq.getFile("file").getInputStream());


                result.put("ok", "数据导入完成!" );
            } catch (Exception e) {

                result.put("fail", e.getMessage() );
                e.printStackTrace();
            }
        }
        System.out.println("ok");
        return JSON.toJSONString(result);
    }



}
