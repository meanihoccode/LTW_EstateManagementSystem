package com.example.ltw_quanlybds;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

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
        return "properties";
    }

    @GetMapping("/contracts")
    public String contracts() {
        return "contracts";
    }

    @GetMapping("/payments")
    public String payments() {
        return "payments";
    }

    @GetMapping("/tenants")
    public String tenants() {
        return "tenants";
    }

    @GetMapping("/owners")
    public String owners() {
        return "owners";
    }

    @GetMapping("/staff")
    public String staff() {
        return "staff";
    }

    @PostMapping("/login")
    public String login() {
        return "redirect:/dashboard";
    }

}
