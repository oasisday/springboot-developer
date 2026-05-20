package me.mingi.springbootdeveloper.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class UserViewController {

    @GetMapping("/login")
    public String login(){
        log.info("login start!!");
        return "oauthLogin";
    }

    @GetMapping("/signup")
    public String signup(){
        log.info("signup start!!");
        return "signup";
    }

}
