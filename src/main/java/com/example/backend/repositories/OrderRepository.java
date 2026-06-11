package com.example.backend.repositories;

import com.example.backend.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    @Query("SELECT DISTINCT o FROM Order o " +
           "LEFT JOIN OrderItem oi ON oi.order.id = o.id " +
           "WHERE (:status IS NULL OR o.status = :status) AND " +
           "(:start IS NULL OR o.registerDate >= :start) AND " +
           "(:end IS NULL OR o.registerDate <= :end) AND " +
           "(:categoryId IS NULL OR oi.product.category.id = :categoryId)")
    List<Order> findFilteredOrders(
            @Param("status") String status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("categoryId") Long categoryId
    );
}
