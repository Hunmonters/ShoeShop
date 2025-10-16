package com.shoeshop.service;

import com.shoeshop.entity.*;
import com.shoeshop.repository.OrderRepository;
import com.shoeshop.repository.OrderDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private CartService cartService;

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Page<Order> findAll(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    public List<Order> findByUsername(String username) {
        return orderRepository.findUserOrders(username);
    }

    @Transactional
    public Order createOrder(Account account, String shipName, String shipPhone,
                             String address, String paymentMethod, String note) {
        // Lấy giỏ hàng
        List<CartItem> cartItems = cartService.getCartItems(account.getUsername());
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // Tạo đơn hàng
        Order order = new Order();
        order.setAccount(account);
        order.setShipName(shipName);
        order.setShipPhone(shipPhone);
        order.setAddress(address);
        order.setPaymentMethod(paymentMethod);
        order.setNote(note);
        order.setStatus((byte) 0); // NEW

        // Tạo chi tiết đơn hàng
        List<OrderDetail> orderDetails = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (CartItem item : cartItems) {
            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(item.getProduct());
            detail.setPrice(item.getProduct().getPrice());
            detail.setQuantity(item.getQuantity());
            detail.setDiscount(BigDecimal.ZERO);

            orderDetails.add(detail);

            BigDecimal lineTotal = detail.getPrice()
                    .multiply(BigDecimal.valueOf(detail.getQuantity()));
            total = total.add(lineTotal);
        }

        order.setOrderDetails(orderDetails);
        order.setTotalAmount(total);

        // Lưu đơn hàng
        Order savedOrder = orderRepository.save(order);

        // Xóa giỏ hàng
        cartService.clearCart(account.getUsername());

        return savedOrder;
    }

    @Transactional
    public Order save(Order order) {
        return orderRepository.save(order);
    }

    @Transactional
    public void updateStatus(Long orderId, Byte status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        orderRepository.save(order);
    }

    @Transactional
    public void deleteById(Long id) {
        orderRepository.deleteById(id);
    }
}