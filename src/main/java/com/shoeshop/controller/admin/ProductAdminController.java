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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/products") // dùng số nhiều cho khớp view
public class ProductAdminController {

    @Autowired private ProductService productService;
    @Autowired private CategoryService categoryService;
    @Autowired private FileService fileService;

    // ✅ Hỗ trợ /admin/products, /admin/products/, và /admin/products/index
    @GetMapping({"", "/", "/index"})
    public String index(@RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        Model model) {

        Page<Product> products = productService.findAll(PageRequest.of(page, size));
        model.addAttribute("products", products);
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("mode", "create");
        return "admin/product";
    }

    // Các phần còn lại giữ nguyên
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        Product product = productService.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        model.addAttribute("products", productService.findAll(PageRequest.of(0, 10)));
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("product", product);
        model.addAttribute("mode", "edit");
        return "admin/product";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Product product,
                       @RequestParam(required = false) MultipartFile imageFile) {
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                String imagePath = fileService.saveFile(imageFile);
                product.setImage(imagePath);
            }
            productService.save(product);
            return "redirect:/admin/products";
        } catch (Exception e) {
            return "redirect:/admin/products?error=" + e.getMessage();
        }
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes ra) {
        productService.deleteById(id);
        ra.addFlashAttribute("success","Đã xoá sản phẩm.");
        return "redirect:/admin/products";
    }

}
