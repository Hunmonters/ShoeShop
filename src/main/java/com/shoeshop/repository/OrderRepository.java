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

    // ====== THÊM CHO DASHBOARD ======

    // Doanh thu hôm nay
    @Query("""
           SELECT COALESCE(SUM(o.totalAmount), 0)
           FROM Order o
           WHERE CAST(o.createDate AS date) = CAST(GETDATE() AS date)
           """)
    Double sumTodayRevenue();

    // Đếm đơn hoàn tất hôm nay (đổi status nếu cần: ví dụ 2 = COMPLETED)
    @Query("""
           SELECT COUNT(o)
           FROM Order o
           WHERE o.status = 2 AND CAST(o.createDate AS date) = CAST(GETDATE() AS date)
           """)
    long countTodayCompleted();

    // Đếm đơn đang xử lý hôm nay (ví dụ 1 = PROCESSING)
    @Query("""
           SELECT COUNT(o)
           FROM Order o
           WHERE o.status = 1 AND CAST(o.createDate AS date) = CAST(GETDATE() AS date)
           """)
    long countTodayPending();

    // 5 đơn gần nhất
    List<Order> findTop5ByOrderByCreateDateDesc();
}
