package com.shoeshop.controller;

import com.shoeshop.entity.Product;
import com.shoeshop.service.ProductService;
import com.shoeshop.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list-by-category/{categoryId}")
    public String listByCategory(
            @PathVariable String categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            Model model) {

        Page<Product> products = productService.findByCategory(categoryId, page, size);

        model.addAttribute("products", products);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("currentCategory", categoryId);

        return "product/product-list";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        Product product = productService.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Lấy sản phẩm cùng loại
        Page<Product> relatedProducts = productService
                .findByCategory(product.getCategory().getId(), 0, 4);

        model.addAttribute("product", product);
        model.addAttribute("relatedProducts", relatedProducts.getContent());

        return "product/product-detail";
    }

    @GetMapping("/search")
    public String search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            Model model) {

        Page<Product> products = productService.searchByName(keyword, page, size);

        model.addAttribute("products", products);
        model.addAttribute("keyword", keyword);
        model.addAttribute("categories", categoryService.findAll());

        return "product/product-list";
    }
}