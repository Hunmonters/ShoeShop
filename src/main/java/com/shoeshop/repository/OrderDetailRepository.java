package com.shoeshop.repository;

import com.shoeshop.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    List<OrderDetail> findByOrderId(Long orderId);

    @Query("SELECT DISTINCT od.product FROM OrderDetail od WHERE od.order.account.username = :username")
    List<Object> findProductsByUsername(String username);
}