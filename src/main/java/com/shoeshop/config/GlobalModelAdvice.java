package com.shoeshop.config;

import com.shoeshop.service.CategoryService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAdvice {
    private final CategoryService categoryService;

    public GlobalModelAdvice(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // Có sẵn ở mọi view: header, product-list, home, admin...
    @ModelAttribute("navCategories")
    public Object navCategories() {
        return categoryService.findAll(); // hoặc findAllByOrderByNameAsc() nếu muốn sắp xếp
    }
}
