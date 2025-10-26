package com.shoeshop.controller.admin;

import com.shoeshop.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;

@Controller
@RequestMapping("/admin/dashboard")
public class DashboardController {

    private final AccountRepository accountRepo;
    private final ProductRepository productRepo;
    private final CategoryRepository categoryRepo;
    private final OrderRepository orderRepo;

    public DashboardController(AccountRepository accountRepo, ProductRepository productRepo,
                               CategoryRepository categoryRepo, OrderRepository orderRepo) {
        this.accountRepo = accountRepo;
        this.productRepo = productRepo;
        this.categoryRepo = categoryRepo;
        this.orderRepo = orderRepo;
    }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("totalUsers", accountRepo.count());
        model.addAttribute("totalProducts", productRepo.count());
        model.addAttribute("totalCategories", categoryRepo.count());
        model.addAttribute("totalOrders", orderRepo.count());

        model.addAttribute("revenueToday", orderRepo.sumTodayRevenue());
        model.addAttribute("ordersDoneToday", orderRepo.countTodayCompleted());
        model.addAttribute("ordersProcessingToday", orderRepo.countTodayPending());
        model.addAttribute("recentOrders", orderRepo.findTop5ByOrderByCreateDateDesc());
        return "admin/dashboard";
    }
}
