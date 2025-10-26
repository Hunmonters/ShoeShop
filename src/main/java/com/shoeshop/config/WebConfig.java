package com.shoeshop.config;

import com.shoeshop.interceptor.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry; // ⬅ import thêm
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/order/**", "/account/edit-profile", "/account/change-password", "/admin/**")
                .excludePathPatterns("/auth/**", "/home/**", "/product/**", "/cart/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:src/main/resources/static/images/");
    }

    // ✅ THÊM: Redirect các đường dẫn cũ về đường dẫn mới
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // /admin/products/index -> /admin/products
        registry.addRedirectViewController("/admin/products/index", "/admin/products");

        // (tuỳ chọn) nếu còn link cũ số ít:
        registry.addRedirectViewController("/admin/product/index", "/admin/products");
        registry.addRedirectViewController("/admin/product", "/admin/products");
        registry.addRedirectViewController("/admin/product/", "/admin/products");
    }
}
