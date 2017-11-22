package com.ccpd.log.service;

import com.ccpd.log.model.RequestMessage;
import com.ccpd.log.model.ResponseMessage;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by jondai on 2017/11/8.
 * log文件处理服务
 */
@Service
public class FileService {


    public String readFile(String path) throws IOException {
        File file =  new File(path);
        StringBuilder stringBuilder = new StringBuilder();

        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String temp = null;
        while ((temp = bufferedReader.readLine()) != null){
            stringBuilder.append(System.lineSeparator() + temp);
        }

        bufferedReader.close();

        return stringBuilder.toString();
    }



    /**
     * 读取指定文件中的内容
     * @param filePath
     * @return
     */
    public String fileReader(String filePath){
        StringBuilder stringBuilder = new StringBuilder();
        try {
            // FileReader 创建一个文件读取句柄
            FileReader fr = new FileReader(filePath);
            // BufferedReader 创建一个文件内容读取句柄
            BufferedReader br = new BufferedReader(fr);

            String str;
            while ((str = br.readLine()) != null) {
                stringBuilder.append(str);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }

    /**
     * 文件写入
     * @param fileName
     * @param context
     */
    public void fileWriter(String fileName, String context){
        try {
            FileWriter fileWriter = new FileWriter(fileName);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            bufferedWriter.write(context);

            bufferedWriter.close();
            fileWriter.close();
            System.out.println("文件写入完成!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public List<RequestMessage> parseXMLToRequest(String filePath){
        //保存转换之后的对象列表
        List<RequestMessage> requestMessages = new ArrayList<>();

        try {
            SAXReader saxReader = new SAXReader();

            //读取xml文件
            Document document = saxReader.read(filePath);

            //获取根节点
            Element rootElement = document.getRootElement();

            //获取到遍历器
            Iterator<Element> modulesIterator = rootElement.elements("Request").iterator();

            //遍历所有的节点
            while (modulesIterator.hasNext()){
                //新建对象，将属性保存到对象中
                RequestMessage requestMessage = new RequestMessage();

                Element moduleElement = modulesIterator.next();

                //拿到Header节点
                Element headerElement = moduleElement.element("Header");

                //赋值到对象
                requestMessage.setTranscationid(headerElement.elementText("TRANSACTIONID"));
                requestMessage.setMessagename(headerElement.elementText("MESSAGENAME"));
                requestMessage.setUsername(headerElement.element("USERNAME").getText());
                requestMessage.setOrgname(headerElement.element("ORGNAME").getText());
                requestMessage.setOrgrrn(headerElement.elementText("ORGREEN"));

                requestMessages.add(requestMessage);
            }

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return requestMessages;
    }

    /**
     * 解析xml文件获取ResponseMessage对象
     * @param xmlFilePath
     * @return
     */
    public List<ResponseMessage> parseXMLToResponseMessage(String xmlFilePath){

        List<ResponseMessage> responseMessages = new ArrayList<>();

        SAXReader saxReader = new SAXReader();

        try {
            //解析xml文本内容
            Document document = saxReader.read(xmlFilePath);
            //获取ROOT根节点
            Element rootElement = document.getRootElement();


            //准备遍历
            Iterator<Element> responseIterator = rootElement.elements("Response").iterator();

            while (responseIterator.hasNext()){

                Element responseElement = responseIterator.next();
                Element header = responseElement.element("Header");

                ResponseMessage responseMessage = new ResponseMessage();

                responseMessage.setTranscationid(header.elementText("TRANSACTIONID"));
                responseMessage.setResult(header.elementText("RESULT"));
                responseMessage.setResultcode(header.elementText("RESULTCODE"));
                responseMessage.setResultmessage(header.elementText("RESULTMESSAGE"));

                responseMessages.add(responseMessage);
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return responseMessages;
    }

}
