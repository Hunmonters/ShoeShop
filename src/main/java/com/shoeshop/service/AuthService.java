package com.shoeshop.service;

import com.shoeshop.entity.Account;
import com.shoeshop.repository.AccountRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SessionService sessionService;
    
    public boolean login(String username, String password, HttpSession session) {
        Optional<Account> account = accountRepository.findByUsername(username);

        if (account.isPresent() && account.get().getActivated()) {
            if (passwordEncoder.matches(password, account.get().getPasswordHash())) {
                // Gán vào session key = "user"
                sessionService.set(session, "user", account.get());
                return true;
            }
        }
        return false;
    }

    public void logout(HttpSession session) {
        sessionService.remove(session, "user");
        session.invalidate();
    }

    public Account getCurrentUser(HttpSession session) {
        return sessionService.get(session, "user");
    }

    public boolean isAuthenticated(HttpSession session) {
        return getCurrentUser(session) != null;
    }

    public boolean isAdmin(HttpSession session) {
        Account user = getCurrentUser(session);
        return user != null && user.getIsAdmin();
    }
}
