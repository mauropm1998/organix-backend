package com.organixui.organixbackend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class MainController {

    @RequestMapping(value = { "/", "/chat/**" })
    public String redirect() {
        return "forward:/index.html";
    }
}
