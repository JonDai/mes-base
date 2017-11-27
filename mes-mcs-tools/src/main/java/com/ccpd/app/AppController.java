package com.ccpd.app;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * Created by jondai on 2017/11/27.
 */
@Controller
@RequestMapping
public class AppController {

    private String message = "JonDai";

    @RequestMapping("/")
    public String welcome(Map<String, Object> model) {
        model.put("username", message);
        return "index";
    }

}
