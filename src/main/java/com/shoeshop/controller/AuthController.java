package com.shoeshop.controller;

import com.shoeshop.entity.Account;
import com.shoeshop.service.AuthService;
import com.shoeshop.service.CookieService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private CookieService cookieService;

    @GetMapping("/login")
    public String showLoginForm(
            HttpServletRequest request,
            Model model) {

        String username = cookieService.getValue(request, "username");
        model.addAttribute("username", username);

        return "account/login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam(required = false) boolean remember,
            HttpSession session,
            HttpServletResponse response,
            Model model) {

        if (authService.login(username, password, session)) {
            // Lưu cookie nếu chọn "Remember me"
            if (remember) {
                cookieService.add(response, "username", username, 24 * 7); // 7 days
            } else {
                cookieService.remove(response, "username");
            }

            // Redirect về trang trước đó hoặc home
            String returnUrl = (String) session.getAttribute("returnUrl");
            session.removeAttribute("returnUrl");
            return "redirect:" + (returnUrl != null ? returnUrl : "/home");
        }

        model.addAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng!");
        model.addAttribute("username", username);
        return "account/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        authService.logout(session);
        return "redirect:/home";
    }
}