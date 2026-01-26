package com.example.ltw_quanlybds;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("/properties")
    public String properties() {
        return "dashboard";
    }

    @GetMapping("/contracts")
    public String contracts() {
        return "dashboard";
    }

    @GetMapping("/payments")
    public String payments() {
        return "dashboard";
    }

    @GetMapping("/tenants")
    public String tenants() {
        return "dashboard";
    }

    @GetMapping("/owners")
    public String owners() {
        return "dashboard";
    }

    @GetMapping("/staff")
    public String staff() {
        return "dashboard";
    }
}
