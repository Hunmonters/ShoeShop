package com.shoeshop.controller.admin;

import com.shoeshop.dto.RevenueReportDTO;
import com.shoeshop.dto.VIPCustomerDTO;
import com.shoeshop.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;

@Controller
@RequestMapping("/admin/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/revenue")
    public String revenue(Model model) {
        List<RevenueReportDTO> report = reportService.getRevenueByCategory();
        model.addAttribute("revenueReport", report);
        return "admin/revenue";
    }

    @GetMapping("/vip")
    public String vip(Model model) {
        List<VIPCustomerDTO> customers = reportService.getTopVIPCustomers();
        model.addAttribute("vipCustomers", customers);
        return "admin/vip";
    }
}