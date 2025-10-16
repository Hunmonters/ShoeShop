package com.shoeshop.service;

import com.shoeshop.entity.Cart;
import com.shoeshop.entity.CartItem;
import com.shoeshop.entity.Product;
import com.shoeshop.entity.Account;
import com.shoeshop.repository.CartRepository;
import com.shoeshop.repository.CartItemRepository;
import com.shoeshop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    public Cart getOrCreateCart(Account account) {
        Optional<Cart> existingCart = cartRepository.findByAccountUsername(account.getUsername());
        if (existingCart.isPresent()) {
            return existingCart.get();
        }

        Cart newCart = new Cart();
        newCart.setAccount(account);
        newCart.setCartItems(new ArrayList<>());
        return cartRepository.save(newCart);
    }

    @Transactional
    public void addToCart(String username, Integer productId, Integer quantity) {
        Cart cart = cartRepository.findByAccountUsername(username)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Optional<CartItem> existingItem = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), productId);

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            cartItemRepository.save(newItem);
        }
    }

    @Transactional
    public void updateCartItem(Long itemId, Integer quantity) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (quantity <= 0) {
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }
    }

    @Transactional
    public void removeFromCart(Long itemId) {
        cartItemRepository.deleteById(itemId);
    }

    @Transactional
    public void clearCart(String username) {
        Cart cart = cartRepository.findByAccountUsername(username)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cart.getCartItems().clear();
        cartRepository.save(cart);
    }

    public List<CartItem> getCartItems(String username) {
        Cart cart = cartRepository.findByAccountUsername(username)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        return cart.getCartItems();
    }

    public BigDecimal getCartTotal(String username) {
        List<CartItem> items = getCartItems(username);
        return items.stream()
                .map(item -> item.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getCartItemCount(String username) {
        List<CartItem> items = getCartItems(username);
        return items.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }
}