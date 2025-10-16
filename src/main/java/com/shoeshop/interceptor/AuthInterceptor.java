package com.shoeshop.interceptor;

import com.shoeshop.entity.Account;
import com.shoeshop.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private AuthService authService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        Account user = authService.getCurrentUser(session);
        String uri = request.getRequestURI();

        // Kiểm tra đăng nhập cho các trang yêu cầu auth
        if (user == null && requiresAuth(uri)) {
            session.setAttribute("returnUrl", uri);
            response.sendRedirect("/auth/login");
            return false;
        }

        // Kiểm tra quyền admin
        if (uri.startsWith("/admin/") && (user == null || !user.getIsAdmin())) {
            response.sendRedirect("/home");
            return false;
        }

        return true;
    }

    private boolean requiresAuth(String uri) {
        return uri.startsWith("/order/") ||
                uri.contains("/edit-profile") ||
                uri.contains("/change-password") ||
                uri.startsWith("/admin/");
    }
}