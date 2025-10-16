package com.shoeshop.controller;

import com.shoeshop.entity.Account;
import com.shoeshop.entity.CartItem;
import com.shoeshop.service.CartService;
import com.shoeshop.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private AuthService authService;

    @GetMapping
    public String viewCart(HttpSession session, Model model) {
        Account user = authService.getCurrentUser(session);
        if (user == null) {
            return "redirect:/auth/login";
        }

        List<CartItem> cartItems = cartService.getCartItems(user.getUsername());
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartTotal", cartService.getCartTotal(user.getUsername()));

        return "cart/cart";
    }

    @PostMapping("/add/{productId}")
    public String addToCart(
            @PathVariable Integer productId,
            @RequestParam(defaultValue = "1") Integer quantity,
            HttpSession session) {

        Account user = authService.getCurrentUser(session);
        if (user == null) {
            return "redirect:/auth/login";
        }

        cartService.addToCart(user.getUsername(), productId, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/update")
    public String updateCart(
            @RequestParam Long itemId,
            @RequestParam Integer quantity,
            HttpSession session) {

        Account user = authService.getCurrentUser(session);
        if (user == null) {
            return "redirect:/auth/login";
        }

        cartService.updateCartItem(itemId, quantity);
        return "redirect:/cart";
    }

    @GetMapping("/remove/{itemId}")
    public String removeFromCart(@PathVariable Long itemId, HttpSession session) {
        Account user = authService.getCurrentUser(session);
        if (user == null) {
            return "redirect:/auth/login";
        }

        cartService.removeFromCart(itemId);
        return "redirect:/cart";
    }

    @GetMapping("/clear")
    public String clearCart(HttpSession session) {
        Account user = authService.getCurrentUser(session);
        if (user == null) {
            return "redirect:/auth/login";
        }

        cartService.clearCart(user.getUsername());
        return "redirect:/cart";
    }
}