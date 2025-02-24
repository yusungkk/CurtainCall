package com.backstage.curtaincall.payment.service;

import com.backstage.curtaincall.global.exception.CustomErrorCode;
import com.backstage.curtaincall.global.exception.CustomException;
import com.backstage.curtaincall.order.entity.Order;
import com.backstage.curtaincall.order.repository.OrderRepository;
import com.backstage.curtaincall.payment.dto.PaymentRequestDto;
import com.backstage.curtaincall.payment.entity.Payment;
import com.backstage.curtaincall.payment.repository.PaymentRepository;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

import static com.backstage.curtaincall.global.exception.CustomErrorCode.*;
import static com.backstage.curtaincall.payment.constant.PaymentConst.I_AM_PORT_STATUS_FAIL_CODE;
import static com.backstage.curtaincall.payment.entity.PaymentStatus.*;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final IamportClient iamportClient;

    @Transactional
    public void createPayment(PaymentRequestDto request) {
        Optional<Order> optionalOrder = orderRepository.findById(request.getOrderId());
        Order findOrder = optionalOrder.orElseThrow(() -> new CustomException(ORDER_NOT_FOUND));

        Payment payment = Payment.create(request, findOrder);
        paymentRepository.save(payment);
    }

    @Transactional
    public void refund(Order order) {
        Optional<Payment> optionalPayment = paymentRepository.findByOrder(order);
        Payment findPayment = optionalPayment.orElseThrow(() -> new CustomException(PAYMENT_NOT_FOUND));

        // 포트원 결제 취소 요청
        IamportResponse<com.siot.IamportRestClient.response.Payment> response;
        try {
            response = iamportClient.cancelPaymentByImpUid(new CancelData(findPayment.getPaymentNo(), true, new BigDecimal(findPayment.getPrice())));
        } catch (IamportResponseException e) {
            throw new CustomException(IAM_PORT_ERROR);
        } catch (IOException e) {
            throw new CustomException(INTERNAL_SERVER_ERROR);
        }

        if (response.getCode() == I_AM_PORT_STATUS_FAIL_CODE) {
            throw new CustomException(CustomErrorCode.PAYMENT_CANCEL_FAIL);
        }

        findPayment.updateStatus(REFUNDED);
    }
}
