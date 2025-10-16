package com.shoeshop.service;

import com.shoeshop.entity.Account;
import com.shoeshop.entity.Role;
import com.shoeshop.repository.AccountRepository;
import com.shoeshop.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    public Optional<Account> findById(String username) {
        return accountRepository.findById(username);
    }

    public Optional<Account> findByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    @Transactional
    public Account save(Account account) {
        return accountRepository.save(account);
    }

    @Transactional
    public Account register(Account account) {
        // Mã hóa mật khẩu
        account.setPasswordHash(passwordEncoder.encode(account.getPasswordHash()));

        // Gán role USER mặc định
        Role userRole = roleRepository.findById("USER").orElseThrow();
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        account.setRoles(roles);

        account.setActivated(false); // Cần kích hoạt qua email
        account.setIsAdmin(false);

        Account saved = accountRepository.save(account);

        // Gửi email kích hoạt
        String activationLink = "http://localhost:8080/account/activate?token=" + saved.getUsername();
        mailService.sendActivationEmail(saved.getEmail(), activationLink);

        return saved;
    }

    @Transactional
    public void updateProfile(Account account) {
        accountRepository.save(account);
    }

    @Transactional
    public void changePassword(String username, String newPassword) {
        Account account = accountRepository.findById(username).orElseThrow();
        account.setPasswordHash(passwordEncoder.encode(newPassword));
        accountRepository.save(account);
    }

    @Transactional
    public void activateAccount(String username) {
        Account account = accountRepository.findById(username).orElseThrow();
        account.setActivated(true);
        accountRepository.save(account);
    }

    public void sendResetPasswordEmail(String email) {
        Account account = accountRepository.findByEmail(email).orElseThrow();
        String resetLink = "http://localhost:8080/account/reset-password?token=" + account.getUsername();
        mailService.sendResetPasswordEmail(email, resetLink);
    }

    @Transactional
    public void deleteById(String username) {
        accountRepository.deleteById(username);
    }

    public boolean existsByEmail(String email) {
        return accountRepository.existsByEmail(email);
    }

    public boolean existsByUsername(String username) {
        return accountRepository.existsByUsername(username);
    }
}