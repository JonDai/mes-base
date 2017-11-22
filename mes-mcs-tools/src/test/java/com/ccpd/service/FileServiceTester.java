package com.ccpd.service;

import com.ccpd.log.model.Constants;
import com.ccpd.log.service.FileService;
import com.ccpd.log.service.StringParseService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.swing.*;

/**
 * Created by jondai on 2017/11/8.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class FileServiceTester {
    String baseFolder = "/Users/jondai/code/panda/mes-base/mes-mcs-log/src/test/java/com/ccpd/service";
    @Autowired
    private  FileService fileService;

    @Autowired
    private StringParseService stringParseService;
    @Test
    public void fileWriter() throws Exception {
//        String testContext = stringParseService.extractXML(Constants.MES_MCS);
        String testContext = stringParseService.extractXML("/Users/jondai/code/panda/mes-base/mes-mcs-log/src/main/resources/log/server-2017-11-08.log");
        fileService.fileWriter(baseFolder +"/server-11-08.xml",testContext);
    }
}
