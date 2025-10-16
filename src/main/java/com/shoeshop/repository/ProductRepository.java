package com.shoeshop.repository;

import com.shoeshop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    Page<Product> findByCategoryId(String categoryId, Pageable pageable);
    Page<Product> findByAvailableTrue(Pageable pageable);
    Page<Product> findByNameContaining(String keyword, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    Page<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    @Query("SELECT p FROM Product p ORDER BY p.createDate DESC")
    List<Product> findNewProducts(Pageable pageable);

    @Query("SELECT p FROM Product p JOIN OrderDetail od ON p.id = od.product.id " +
            "GROUP BY p.id, p.name, p.image, p.price, p.quantity, p.createDate, p.available, p.category " +
            "ORDER BY SUM(od.quantity) DESC")
    List<Product> findBestSellers(Pageable pageable);
}