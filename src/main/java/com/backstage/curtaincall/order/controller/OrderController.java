package com.backstage.curtaincall.order.controller;

import com.backstage.curtaincall.order.dto.OrderRequestDto;
import com.backstage.curtaincall.order.dto.OrderResponseDto;
import com.backstage.curtaincall.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 예약된 좌석 조회 API
    @GetMapping("/reserved-seats")
    public ResponseEntity<List<String>> getReservedSeats(@RequestParam Long productDetailId) {
        List<String> reservedSeats = orderService.getReservedSeats(productDetailId);
        return ResponseEntity.ok(reservedSeats);
    }

    // 주문 생성
    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestBody OrderRequestDto orderRequestDto) {
        OrderResponseDto response = orderService.createOrder(orderRequestDto);
        return ResponseEntity.ok(response);
    }
}
