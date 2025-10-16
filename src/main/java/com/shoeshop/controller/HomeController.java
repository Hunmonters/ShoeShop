package com.shoeshop.controller;

import com.shoeshop.entity.Product;
import com.shoeshop.service.ProductService;
import com.shoeshop.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;

@Controller
@RequestMapping("/")
public class HomeController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping({"/", "/home", "/home/index"})
    public String index(Model model) {
        // Lấy sản phẩm bán chạy
        List<Product> bestSellers = productService.findBestSellers(8);

        // Lấy sản phẩm mới
        List<Product> newProducts = productService.findNewProducts(8);

        model.addAttribute("bestSellers", bestSellers);
        model.addAttribute("newProducts", newProducts);
        model.addAttribute("categories", categoryService.findAll());

        return "home";
    }
}