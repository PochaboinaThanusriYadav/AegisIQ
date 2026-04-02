package com.example.AegisIQ.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaForwardController {

    @GetMapping({"/", "/login", "/register", "/dashboard", "/report", "/admin", "/incident/{id}"})
    public String forwardToIndex() {
        return "forward:/index.html";
    }
}