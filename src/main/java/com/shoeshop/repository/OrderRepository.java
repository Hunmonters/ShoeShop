package com.shoeshop.repository;

import com.shoeshop.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByAccountUsername(String username, Pageable pageable);
    List<Order> findByAccountUsernameOrderByCreateDateDesc(String username);

    @Query("SELECT o FROM Order o WHERE o.account.username = :username ORDER BY o.createDate DESC")
    List<Order> findUserOrders(String username);
}