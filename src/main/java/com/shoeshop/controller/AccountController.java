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

    // --- Đăng ký ---
    @GetMapping("/sign-up")
    public String showSignUpForm(Model model) {
        model.addAttribute("account", new Account());
        return "account/sign-up";
    }

    @PostMapping("/sign-up")
    public String signUp(@Valid @ModelAttribute Account account,
                         BindingResult result,
                         Model model) {

        if (result.hasErrors()) {
            return "account/sign-up";
        }

        // Kiểm tra username
        if (accountService.existsByUsername(account.getUsername())) {
            model.addAttribute("error", "Tên đăng nhập đã tồn tại!");
            return "account/sign-up";
        }

        // Kiểm tra email
        if (accountService.existsByEmail(account.getEmail())) {
            model.addAttribute("error", "Email đã được sử dụng!");
            return "account/sign-up";
        }

        try {
            accountService.register(account);
            model.addAttribute("success", "Đăng ký thành công! Vui lòng đăng nhập.");
            return "account/login";
        } catch (Exception e) {
            model.addAttribute("error", "Đăng ký thất bại: " + e.getMessage());
            return "account/sign-up";
        }
    }

    // --- Kích hoạt ---
    @GetMapping("/activate")
    public String activateAccount(@RequestParam String token, Model model) {
        try {
            accountService.activateAccount(token);
            model.addAttribute("success", "Kích hoạt tài khoản thành công!");
        } catch (Exception e) {
            model.addAttribute("error", "Kích hoạt thất bại!");
        }
        return "account/login";
    }

    // --- Hồ sơ cá nhân ---
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
    public String editProfile(@ModelAttribute Account account,
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

        // Cập nhật lại session để navbar hiển thị đúng
        session.setAttribute("user", currentUser);

        model.addAttribute("success", "Cập nhật thông tin thành công!");
        model.addAttribute("account", currentUser);
        return "account/edit-profile";
    }

    // --- Đổi mật khẩu ---
    @GetMapping("/change-password")
    public String showChangePassword(HttpSession session, Model model) {
        if (authService.getCurrentUser(session) == null) {
            return "redirect:/auth/login";
        }
        return "account/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String oldPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 HttpSession session,
                                 Model model) {

        Account user = authService.getCurrentUser(session);
        if (user == null) {
            return "redirect:/auth/login";
        }

        // Kiểm tra khớp mật khẩu mới
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Mật khẩu mới không khớp!");
            return "account/change-password";
        }

        try {
            boolean changed = accountService.changePassword(user.getUsername(), oldPassword, newPassword);
            if (changed) {
                model.addAttribute("success", "Đổi mật khẩu thành công!");
            } else {
                model.addAttribute("error", "Mật khẩu cũ không đúng!");
            }
        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi khi đổi mật khẩu: " + e.getMessage());
        }

        return "account/change-password";
    }

    // --- Quên mật khẩu ---
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

    // --- Mở form đặt lại mật khẩu (token = username) ---
    @GetMapping("/reset-password")
    public String showResetPassword(@RequestParam String token, Model model) {
        model.addAttribute("token", token);
        return "account/reset-password";
    }

    // --- Đặt lại mật khẩu ---
    @PostMapping("/reset-password")
    public String doResetPassword(@RequestParam String token,
                                  @RequestParam String newPassword,
                                  @RequestParam String confirmPassword,
                                  Model model) {
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Mật khẩu mới không khớp!");
            model.addAttribute("token", token);
            return "account/reset-password";
        }
        try {
            accountService.resetPasswordByToken(token, newPassword);
            model.addAttribute("success", "Đổi mật khẩu thành công! Vui lòng đăng nhập.");
            return "account/login";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("token", token);
            return "account/reset-password";
        }
    }
}
