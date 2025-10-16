package com.shoeshop.controller;

import com.shoeshop.entity.Account;
import com.shoeshop.service.AccountService;
import com.shoeshop.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AuthService authService;

    @GetMapping("/sign-up")
    public String showSignUpForm(Model model) {
        model.addAttribute("account", new Account());
        return "account/sign-up";
    }

    @PostMapping("/sign-up")
    public String signUp(
            @Valid @ModelAttribute Account account,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            return "account/sign-up";
        }

        // Kiểm tra username đã tồn tại
        if (accountService.existsByUsername(account.getUsername())) {
            model.addAttribute("error", "Tên đăng nhập đã tồn tại!");
            return "account/sign-up";
        }

        // Kiểm tra email đã tồn tại
        if (accountService.existsByEmail(account.getEmail())) {
            model.addAttribute("error", "Email đã được sử dụng!");
            return "account/sign-up";
        }

        try {
            accountService.register(account);
            model.addAttribute("success", "Đăng ký thành công! Vui lòng kiểm tra email để kích hoạt tài khoản.");
            return "account/login";
        } catch (Exception e) {
            model.addAttribute("error", "Đăng ký thất bại: " + e.getMessage());
            return "account/sign-up";
        }
    }

    @GetMapping("/activate")
    public String activateAccount(@RequestParam String token, Model model) {
        try {
            accountService.activateAccount(token);
            model.addAttribute("success", "Kích hoạt tài khoản thành công!");
            return "account/login";
        } catch (Exception e) {
            model.addAttribute("error", "Kích hoạt thất bại!");
            return "account/login";
        }
    }

    @GetMapping("/edit-profile")
    public String showEditProfile(HttpSession session, Model model) {
        Account user = authService.getCurrentUser(session);
        if (user == null) {
            return "redirect:/auth/login";
        }

        model.addAttribute("account", user);
        return "account/edit-profile";
    }

    @PostMapping("/edit-profile")
    public String editProfile(
            @ModelAttribute Account account,
            HttpSession session,
            Model model) {

        Account currentUser = authService.getCurrentUser(session);
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        // Cập nhật thông tin
        currentUser.setFullname(account.getFullname());
        currentUser.setEmail(account.getEmail());
        currentUser.setAddress(account.getAddress());
        currentUser.setPhone(account.getPhone());

        accountService.updateProfile(currentUser);

        model.addAttribute("success", "Cập nhật thông tin thành công!");
        model.addAttribute("account", currentUser);
        return "account/edit-profile";
    }

    @GetMapping("/change-password")
    public String showChangePassword(HttpSession session) {
        Account user = authService.getCurrentUser(session);
        if (user == null) {
            return "redirect:/auth/login";
        }
        return "account/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            HttpSession session,
            Model model) {

        Account user = authService.getCurrentUser(session);
        if (user == null) {
            return "redirect:/auth/login";
        }

        // Kiểm tra mật khẩu mới khớp
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Mật khẩu mới không khớp!");
            return "account/change-password";
        }

        try {
            accountService.changePassword(user.getUsername(), newPassword);
            model.addAttribute("success", "Đổi mật khẩu thành công!");
        } catch (Exception e) {
            model.addAttribute("error", "Đổi mật khẩu thất bại!");
        }

        return "account/change-password";
    }

    @GetMapping("/forgot-password")
    public String showForgotPassword() {
        return "account/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String email, Model model) {
        try {
            accountService.sendResetPasswordEmail(email);
            model.addAttribute("success", "Link đặt lại mật khẩu đã được gửi đến email của bạn!");
        } catch (Exception e) {
            model.addAttribute("error", "Email không tồn tại trong hệ thống!");
        }
        return "account/forgot-password";
    }
}