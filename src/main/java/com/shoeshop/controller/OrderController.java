package com.shoeshop.controller;

import com.shoeshop.entity.Account;
import com.shoeshop.entity.Order;
import com.shoeshop.entity.CartItem;
import com.shoeshop.entity.Product;
import com.shoeshop.service.OrderService;
import com.shoeshop.service.AuthService;
import com.shoeshop.service.CartService;
import com.shoeshop.repository.OrderDetailRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private AuthService authService;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @GetMapping("/checkout")
    public String showCheckout(HttpSession session, Model model) {
        Account user = authService.getCurrentUser(session);
        if (user == null) {
            return "redirect:/auth/login";
        }

        List<CartItem> cartItems = cartService.getCartItems(user.getUsername());
        if (cartItems.isEmpty()) {
            return "redirect:/cart";
        }

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartTotal", cartService.getCartTotal(user.getUsername()));
        model.addAttribute("account", user);

        return "order/checkout";
    }

    @PostMapping("/checkout")
    public String checkout(
            @RequestParam String shipName,
            @RequestParam String shipPhone,
            @RequestParam String address,
            @RequestParam String paymentMethod,
            @RequestParam(required = false) String note,
            HttpSession session,
            Model model) {

        Account user = authService.getCurrentUser(session);
        if (user == null) {
            return "redirect:/auth/login";
        }

        try {
            Order order = orderService.createOrder(user, shipName, shipPhone,
                    address, paymentMethod, note);
            return "redirect:/order/detail/" + order.getId();
        } catch (Exception e) {
            model.addAttribute("error", "Đặt hàng thất bại: " + e.getMessage());
            return "order/checkout";
        }
    }

    @GetMapping("/list")
    public String listOrders(HttpSession session, Model model) {
        Account user = authService.getCurrentUser(session);
        if (user == null) {
            return "redirect:/auth/login";
        }

        List<Order> orders = orderService.findByUsername(user.getUsername());
        model.addAttribute("orders", orders);

        return "order/order-list";
    }

    @GetMapping("/detail/{id}")
    public String orderDetail(@PathVariable Long id, HttpSession session, Model model) {
        Account user = authService.getCurrentUser(session);
        if (user == null) {
            return "redirect:/auth/login";
        }

        Order order = orderService.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Kiểm tra quyền xem đơn hàng
        if (!order.getAccount().getUsername().equals(user.getUsername())) {
            return "redirect:/order/list";
        }

        model.addAttribute("order", order);
        return "order/order-detail";
    }

    @GetMapping("/my-product-list")
    public String myProducts(HttpSession session, Model model) {
        Account user = authService.getCurrentUser(session);
        if (user == null) {
            return "redirect:/auth/login";
        }

        List<Object> products = orderDetailRepository.findProductsByUsername(user.getUsername());
        model.addAttribute("products", products);

        return "order/my-product-list";
    }
}