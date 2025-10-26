package com.shoeshop.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    // ✅ Chỉ redirect về dashboard
    @GetMapping({"", "/"})
    public String adminHome() {
        return "redirect:/admin/dashboard";
    }
}
