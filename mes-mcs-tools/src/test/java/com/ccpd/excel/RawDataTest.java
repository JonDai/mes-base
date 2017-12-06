package com.ccpd.excel;

import com.ccpd.excel.service.RawDataService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by jondai on 2017/12/5.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RawDataTest {

    @Autowired
    private RawDataService rawDataService;

    String path = "/Users/jondai/Documents/excel统计测试/原始数据.xlsx";

    @Test
    public void readAndSaveRawDataFile() throws Exception {
        rawDataService.readAndSaveRawDataFile(path);
    }
}
