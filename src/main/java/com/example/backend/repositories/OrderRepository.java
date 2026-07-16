package com.example.backend.repositories;

import com.example.backend.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.time.LocalDateTime;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByStripeSessionId(String stripeSessionId);
    List<Order> findAllByOrderByRegisterDateDesc();
    List<Order> findByPersonaIdOrderByRegisterDateDesc(Long personaId);
    List<Order> findByRegisterDateGreaterThanEqualAndRegisterDateLessThan(LocalDateTime start, LocalDateTime end);
}
