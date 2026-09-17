package com.example.LoginPUC.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// @RestController - API REST BACK END
// @Controller - FRONT END, HTML, CSS, JS, JSP, Thymeleaf

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(){
        return "login";
    }
}
