package com.shoeshop.repository;

import com.shoeshop.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, String> {
    List<Category> findAllByOrderByNameAsc(); // dùng nếu muốn menu theo ABC
}
