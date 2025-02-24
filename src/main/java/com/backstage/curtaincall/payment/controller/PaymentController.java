package com.backstage.curtaincall.payment.controller;

import com.backstage.curtaincall.payment.dto.PaymentRequestDto;
import com.backstage.curtaincall.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @ResponseStatus(CREATED)
    @PostMapping
    public void createPayment(@RequestBody PaymentRequestDto request) {
        paymentService.createPayment(request);
    }
}
