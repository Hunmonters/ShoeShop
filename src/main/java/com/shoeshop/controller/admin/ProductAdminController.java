package com.shoeshop.controller.admin;

import com.shoeshop.entity.Product;
import com.shoeshop.service.ProductService;
import com.shoeshop.service.CategoryService;
import com.shoeshop.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/admin/product")
public class ProductAdminController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private FileService fileService;

    @GetMapping("/index")
    public String index(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Page<Product> products = productService.findAll(PageRequest.of(page, size));

        model.addAttribute("products", products);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("product", new Product());

        return "admin/product";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        Product product = productService.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        model.addAttribute("products", productService.findAll(PageRequest.of(0, 10)));
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("product", product);

        return "admin/product";
    }

    @PostMapping("/create")
    public String create(
            @ModelAttribute Product product,
            @RequestParam(required = false) MultipartFile imageFile) {

        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                String imagePath = fileService.saveFile(imageFile);
                product.setImage(imagePath);
            }

            productService.save(product);
            return "redirect:/admin/product/index";
        } catch (Exception e) {
            return "redirect:/admin/product/index?error=" + e.getMessage();
        }
    }

    @PostMapping("/update")
    public String update(
            @ModelAttribute Product product,
            @RequestParam(required = false) MultipartFile imageFile) {

        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                String imagePath = fileService.saveFile(imageFile);
                product.setImage(imagePath);
            }

            productService.save(product);
            return "redirect:/admin/product/index";
        } catch (Exception e) {
            return "redirect:/admin/product/index?error=" + e.getMessage();
        }
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        productService.deleteById(id);
        return "redirect:/admin/product/index";
    }
}