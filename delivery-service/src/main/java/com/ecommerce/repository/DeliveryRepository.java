package com.ecommerce.repository;

import com.ecommerce.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    Optional<Delivery> findByEventId(String eventId);

    Optional<Delivery> findByOrderId(Long orderId);

    boolean existsByEventId(String eventId);

    boolean existsByOrderId(Long orderId);
}
