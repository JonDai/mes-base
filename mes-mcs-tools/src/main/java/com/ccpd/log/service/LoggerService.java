package com.ccpd.log.service;

import com.ccpd.log.dao.RequestMessageDao;
import com.ccpd.log.dao.ResponseMessageDao;
import com.ccpd.log.model.Constants;
import com.ccpd.log.model.RequestMessage;
import com.ccpd.log.model.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Created by jondai on 2017/11/8.
 */
@Service
public class LoggerService {

    @Autowired
    private FileService fileService;

    @Autowired
    private StringParseService stringParseService;

    @Autowired
    private ResponseMessageDao responseMessageDao;

    @Autowired
    private RequestMessageDao requestMessageDao;


    /**
     * 模拟消息测试
     */
    public void simulateMessageTest(){
        Iterable<RequestMessage> all = requestMessageDao.findAll();

        System.out.println("准备开始测试!");
        for (RequestMessage requestMessage : all) {
            System.out.print("GTM 向 MCS发送发送: "+ requestMessage.getMessagename() + " 消息ID: " + requestMessage.getTranscationid());
            System.out.print("  ......  ");

            ResponseMessage one = responseMessageDao.findOne(requestMessage.getTranscationid());
            if(one != null && !StringUtils.isEmpty(one.getTranscationid())){
                System.out.print("MCS 回复:  收到并返回消息");
            }else{
                System.out.print("MCS 消息已丢失..........");
            }

            System.out.println("");
        }
    }

    /**
     * 比较消息，拿到最新的并且有收到回复的消息。
     */
    public void completeMessageTest(){
        Iterable<RequestMessage> all = requestMessageDao.findAll();

        Map<String,String> flow = new HashMap<>();
        for (RequestMessage requestMessage : all) {
//            if("AreYouThereReq".equals(requestMessage.getMessagename())){
//                flow.clear();
//            }else{
                ResponseMessage one = responseMessageDao.findOne(requestMessage.getTranscationid());
                if(one != null && !StringUtils.isEmpty(one.getTranscationid())){
                    flow.put(requestMessage.getMessagename(),requestMessage.getTranscationid());
//                }
            }
        }
        for (String s : flow.keySet()) {
            System.out.println("消息 " + s + " ID: " + flow.get(s));
        }
    }

    /**
     * 将解析的Request xml文件保存到数据库中
     */
    public void saveRequestMessageByXML(String filePath){
        List<RequestMessage> requestMessages = fileService.parseXMLToRequest(filePath);

        requestMessageDao.save(requestMessages);

    }


    /**
     * 将解析的Response xml文件保存到数据库中
     */
    public void saveResponseMessageByXML(String filePath){
        List<ResponseMessage> responseMessages = fileService.parseXMLToResponseMessage(filePath);

        responseMessageDao.save(responseMessages);
    }


    /**
     * 解析xml中RequestMessage， 并打印到控制台
     * @return
     */
    public List<String> convertToRequestMessage(){

        List<RequestMessage> requestMessages = fileService.parseXMLToRequest(Constants.MCS_XML);

        String mcsString = stringParseService.extractXML(Constants.MES_MCS);

        System.out.println("准备开始消息测试!");
        int i = 1;
        for (RequestMessage requestMessage : requestMessages) {


            System.out.print(i+" MCS发送:" + requestMessage.getMessagename() + "  ID为："+ requestMessage.getTranscationid());
            boolean isContains = mcsString.contains(requestMessage.getTranscationid());
            String mcsResponse = "";
            if (isContains)
                mcsResponse = " 收到请求并返回响应" ;
            else
                mcsResponse = "x x x x x 消息丢失了 x x x x x";

            System.out.print(" ...... MES响应:" +  mcsResponse);
            System.out.println("");
            i++;
//            System.out.println(requestMessage);
        }

        return null;
    }


}
