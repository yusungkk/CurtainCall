package com.backstage.curtaincall.payment.repository;

import com.backstage.curtaincall.order.entity.Order;
import com.backstage.curtaincall.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrder(Order order);
}
