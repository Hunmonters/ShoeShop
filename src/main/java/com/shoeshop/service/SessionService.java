package com.shoeshop.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class SessionService {

    public <T> T get(HttpSession session, String name) {
        return (T) session.getAttribute(name);
    }

    public void set(HttpSession session, String name, Object value) {
        session.setAttribute(name, value);
    }

    public void remove(HttpSession session, String name) {
        session.removeAttribute(name);
    }
}