package com.ccpd.log.service;

import com.ccpd.log.model.Constants;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * Created by jondai on 2017/11/8.
 */
@Service
public class StringParseService {



    /**
     * 提取文本文件中XML有效的内容
     * @param filePath
     * @return
     */
    public String extractXML(String filePath){
        StringBuilder stringBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines(Paths.get(filePath))) {

            stream.filter(line -> line.trim().startsWith("<"))
                    .forEach(s -> stringBuilder.append(s).append("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        stringBuilder.insert(0,"<ROOT>\n");
        stringBuilder.append("</ROOT>");
        return stringBuilder.toString();
    }


}
