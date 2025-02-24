package com.backstage.curtaincall.payment.service;

import com.backstage.curtaincall.global.exception.CustomErrorCode;
import com.backstage.curtaincall.global.exception.CustomException;
import com.backstage.curtaincall.order.entity.Order;
import com.backstage.curtaincall.order.repository.OrderRepository;
import com.backstage.curtaincall.payment.dto.PaymentRequestDto;
import com.backstage.curtaincall.payment.entity.Payment;
import com.backstage.curtaincall.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public void createPayment(PaymentRequestDto request) {
        Optional<Order> optionalOrder = orderRepository.findById(request.getOrderId());
        Order findOrder = optionalOrder.orElseThrow(() -> new CustomException(CustomErrorCode.ORDER_NOT_FOUND));

        Payment payment = Payment.create(request, findOrder);
        paymentRepository.save(payment);
    }
}
