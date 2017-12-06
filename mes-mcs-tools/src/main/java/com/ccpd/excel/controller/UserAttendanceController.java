package com.ccpd.excel.controller;

import com.alibaba.fastjson.JSON;
import com.ccpd.excel.service.UserAttendanceExcel;
import org.apache.poi.ss.usermodel.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jondai on 2017/11/27.
 */

@Controller
@RequestMapping("excel")
public class UserAttendanceController {

    @Autowired
    private UserAttendanceExcel userAttendanceExcel;

    @RequestMapping("toGenerate")
    public String toGenerate(){
        return "excel/generate";
    }



    @RequestMapping("generate")
    @ResponseBody
    public String generate() {
        Map<String,String> result = new HashMap<>();
        try {
            String filePath = userAttendanceExcel.generateExcel() ;

            result.put("ok", "统计文件生成完成!");
            result.put("path",filePath);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("fail", e.getMessage());
        }

        return JSON.toJSONString(result);
    }

}
