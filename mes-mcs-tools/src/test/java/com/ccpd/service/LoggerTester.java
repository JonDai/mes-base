package com.ccpd.service;

import com.ccpd.log.dao.RequestMessageDao;
import com.ccpd.log.model.Constants;
import com.ccpd.log.model.RequestMessage;
import com.ccpd.log.service.FileService;
import com.ccpd.log.service.LoggerService;
import com.ccpd.log.service.StringParseService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CountDownLatch;

/**
 * Created by jondai on 2017/11/8.
 */

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class LoggerTester {
    @Autowired
    private LoggerService loggerService;

    @Autowired
    private StringParseService stringParseService;

    @Autowired
    private RequestMessageDao requestMessageDao;

    private String xmlpath = "/Users/jondai/code/panda/mes-base/mes-mcs-log/src/test/java/com/ccpd/service/server-11-09.xml";

    @Test
    public void convertToRequestMessage() throws Exception {

        loggerService.convertToRequestMessage();
    }


//    @Test
//    public void saveRequestMessageByXML() throws Exception {
//
//
//
//        loggerService.saveRequestMessageByXML();
//    }
//
//    @Test
//    public void saveResponseMessageByXML() throws Exception {
//        loggerService.saveResponseMessageByXML();
//    }

    @Test
    public void simulateMessageTest() throws Exception {

        loggerService.simulateMessageTest();
    }

    @Test
    public void completeMessageTest() throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(2);

        new Thread(() -> {
            loggerService.saveRequestMessageByXML(xmlpath);
            countDownLatch.countDown();
        }).start();

        new Thread(() -> {
            loggerService.saveResponseMessageByXML(xmlpath);
            countDownLatch.countDown();
        }).start();

        countDownLatch.await();
        System.out.println("完成");
//        loggerService.completeMessageTest();
    }

    @Test
    public void completeMessageAll() throws Exception {
        loggerService.completeMessageTest();
    }

    @Test
    public void testThread() throws Exception {
        Thread daoThread = new Thread(() -> {

//            System.out.println("hello >>>>>>>>>>>>>>>>>>>>>>>>>>>");
//            System.out.println(Thread.currentThread().getName());
////            loggerService.test();
//            RequestMessage one = requestMessageDao.findOne("201711081031058003");
//            System.out.println(one.toString());
            while (true){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("这是new线程...");
            }

        });
        daoThread.start();
        while (true){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("这是主线程");
        }

//        daoThread.join();
    }
}
