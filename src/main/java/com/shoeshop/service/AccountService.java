package com.shoeshop.service;

import com.shoeshop.entity.Account;
import com.shoeshop.entity.Role;
import com.shoeshop.repository.AccountRepository;
import com.shoeshop.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MailService mailService;

    // Lấy tất cả account
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    // Tìm account theo username
    public Optional<Account> findById(String username) {
        return accountRepository.findById(username);
    }

    // Tìm account theo email
    public Optional<Account> findByEmail(String email) {
        return accountRepository.findByEmail(email.trim());
    }

    @Transactional
    public Account save(Account account) {
        return accountRepository.save(account);
    }

    // Đăng ký tài khoản mới
    @Transactional
    public Account register(Account account) {
        String rawPassword = account.getPassword();
        if (rawPassword == null || rawPassword.isEmpty()) {
            throw new RuntimeException("Mật khẩu không được để trống");
        }
        account.setPasswordHash(passwordEncoder.encode(rawPassword));

        // Gán role USER mặc định
        Role userRole = roleRepository.findById("USER")
                .orElseThrow(() -> new RuntimeException("Role USER not found"));
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        account.setRoles(roles);

        account.setActivated(true);
        account.setIsAdmin(false);

        return accountRepository.save(account);
    }

    // Cập nhật thông tin cá nhân
    @Transactional
    public void updateProfile(Account account) {
        accountRepository.save(account);
    }

    // Đổi mật khẩu có kiểm tra mật khẩu cũ
    @Transactional
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        return accountRepository.findById(username).map(account -> {
            if (!passwordEncoder.matches(oldPassword, account.getPasswordHash())) {
                return false;
            }
            account.setPasswordHash(passwordEncoder.encode(newPassword));
            accountRepository.save(account);
            return true;
        }).orElse(false);
    }

    // Kích hoạt tài khoản
    @Transactional
    public void activateAccount(String username) {
        Account account = accountRepository.findById(username)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        account.setActivated(true);
        accountRepository.save(account);
    }

    // --- QUÊN MẬT KHẨU (cách nhanh: token = username) ---
    // Gửi email reset password
    public void sendResetPasswordEmail(String email) {
        String cleanEmail = email.trim();
        Account account = accountRepository.findByEmailIgnoreCase(cleanEmail)
                .orElseThrow(() -> new RuntimeException("Email không tồn tại trong hệ thống!"));

        String resetLink = "http://localhost:8080/account/reset-password?token=" + account.getUsername();

        // Gửi kèm thông tin tài khoản
        mailService.sendResetPasswordEmail(account, resetLink);
    }


    // Đặt lại mật khẩu khi token chính là username (cách nhanh)
    @Transactional
    public void resetPasswordByToken(String tokenUsername, String newPassword) {
        Account acc = accountRepository.findById(tokenUsername)
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));
        if (newPassword == null || newPassword.isBlank()) {
            throw new RuntimeException("Mật khẩu mới không được để trống");
        }
        acc.setPasswordHash(passwordEncoder.encode(newPassword));
        accountRepository.save(acc);
    }

    @Transactional
    public void deleteById(String username) {
        accountRepository.deleteById(username);
    }

    public boolean existsByEmail(String email) {
        return accountRepository.existsByEmail(email.trim());
    }

    public boolean existsByUsername(String username) {
        return accountRepository.existsByUsername(username.trim());
    }
}
