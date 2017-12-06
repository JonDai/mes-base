package com.ccpd.app;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * Created by jondai on 2017/11/27.
 */
@Controller
@RequestMapping
public class AppController {

    private String username = "管理员";

    @RequestMapping("/")
    public String welcome(ModelMap model) {
        model.put("username", username);
        return "index";
    }

}
