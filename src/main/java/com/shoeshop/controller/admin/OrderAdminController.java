package com.shoeshop.controller.admin;

import com.shoeshop.entity.Order;
import com.shoeshop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/order")
public class OrderAdminController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/index")
    public String index(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Page<Order> orders = orderService.findAll(PageRequest.of(page, size));
        model.addAttribute("orders", orders);

        return "admin/order";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Order order = orderService.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        model.addAttribute("order", order);
        model.addAttribute("orders", orderService.findAll(PageRequest.of(0, 10)));

        return "admin/order";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute Order order) {
        orderService.save(order);
        return "redirect:/admin/order/index";
    }

    @PostMapping("/update-status")
    public String updateStatus(
            @RequestParam Long orderId,
            @RequestParam Byte status) {

        orderService.updateStatus(orderId, status);
        return "redirect:/admin/order/index";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        orderService.deleteById(id);
        return "redirect:/admin/order/index";
    }
}