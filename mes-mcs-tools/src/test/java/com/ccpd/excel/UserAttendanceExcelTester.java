package com.ccpd.excel;

import com.ccpd.excel.dao.UserAttendanceDao;
import com.ccpd.excel.model.UserAttnStatistics;
import com.ccpd.excel.service.UserAttendanceExcel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * Created by jondai on 2017/11/22.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserAttendanceExcelTester {

    @Autowired
    private UserAttendanceExcel userAttendanceExcel;



    String path = "/Users/jondai/Documents/excel统计测试/20171106-1112汇总.xlsx";
    String path1 = "/Users/jondai/Documents/excel统计测试/20171106-1108.xlsx";
    String path2 = "/Users/jondai/Documents/excel统计测试/副本 20171120-1122汇总.xlsx";
    String path3 = "/Users/jondai/Documents/excel统计测试/汇总1.xlsx";

    @Test
    public void readUserAttendanceFile() throws Exception {
        userAttendanceExcel.readAndSaveUserAttendanceFile(path3);
    }


    @Test
    public void AnalysisUserAtteByManufacturerandDept() throws Exception {
//        userAttendanceExcel.readAndSaveUserAttendanceFile(path3);
        UserAttnStatistics array = userAttendanceExcel.AnalysisUserAtteByManufacturerandDept("NIKON", "ARRAY");

        System.out.println(array);
    }

    @Test
    public void AnalysisUserAtteByDept() throws Exception {
        List<UserAttnStatistics> userAttnStatisticss = userAttendanceExcel.AnalysisUserAtteByDept("CF");
        for (UserAttnStatistics userAttnStatistics : userAttnStatisticss) {
            System.out.println(userAttnStatistics);
        }
        System.out.println(1);
    }

    @Test
    public void generateExcel() throws Exception {
        userAttendanceExcel.generateExcel();

    }
}


