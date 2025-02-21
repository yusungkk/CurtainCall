package com.backstage.curtaincall.payment.repository;

import com.backstage.curtaincall.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

}
