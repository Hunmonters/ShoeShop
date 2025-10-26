package com.shoeshop.service;

import com.shoeshop.entity.Product;
import com.shoeshop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Optional<Product> findById(Integer id) {
        return productRepository.findById(id);
    }

    public Page<Product> findByCategory(String categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findByCategory_Id(categoryId, pageable); // ✅
    }


    public Page<Product> searchByName(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findByNameContaining(keyword, pageable);
    }

    public Page<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findByPriceRange(minPrice, maxPrice, pageable);
    }

    public List<Product> findNewProducts(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return productRepository.findNewProducts(pageable);
    }

    public List<Product> findBestSellers(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return productRepository.findBestSellers(pageable);
    }

    @Transactional
    public Product save(Product product) {
        return productRepository.save(product);
    }

    @Transactional
    public void deleteById(Integer id) {
        productRepository.deleteById(id);
    }
}