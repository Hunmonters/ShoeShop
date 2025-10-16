package com.shoeshop.service;

import com.shoeshop.dto.RevenueReportDTO;
import com.shoeshop.dto.VIPCustomerDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ReportService {

    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public List<RevenueReportDTO> getRevenueByCategory() {
        String sql = "SELECT c.name AS categoryName, " +
                "SUM((od.price * od.quantity) * (1 - od.discount/100.0)) AS totalRevenue, " +
                "SUM(od.quantity) AS totalQuantity, " +
                "MAX(od.price) AS maxPrice, " +
                "MIN(od.price) AS minPrice, " +
                "AVG(od.price) AS avgPrice " +
                "FROM OrderDetails od " +
                "JOIN Products p ON od.productId = p.id " +
                "JOIN Categories c ON p.categoryId = c.id " +
                "JOIN Orders o ON od.orderId = o.id " +
                "WHERE o.status IN (1, 2, 3) " +
                "GROUP BY c.name " +
                "ORDER BY totalRevenue DESC";

        return entityManager.createNativeQuery(sql, RevenueReportDTO.class).getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<VIPCustomerDTO> getTopVIPCustomers() {
        String sql = "SELECT TOP 10 " +
                "a.fullname AS customerName, " +
                "SUM((od.price * od.quantity) * (1 - od.discount/100.0)) AS totalSpent, " +
                "MIN(o.createDate) AS firstOrderDate, " +
                "MAX(o.createDate) AS lastOrderDate " +
                "FROM Orders o " +
                "JOIN OrderDetails od ON o.id = od.orderId " +
                "JOIN Accounts a ON o.username = a.username " +
                "WHERE o.status IN (1, 2, 3) " +
                "GROUP BY a.fullname " +
                "ORDER BY totalSpent DESC";

        return entityManager.createNativeQuery(sql, VIPCustomerDTO.class).getResultList();
    }
}