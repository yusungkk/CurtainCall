package com.backstage.curtaincall.order.controller;

import com.backstage.curtaincall.global.exception.CustomException;
import com.backstage.curtaincall.order.dto.OrderRequestDto;
import com.backstage.curtaincall.order.dto.OrderResponseDto;
import com.backstage.curtaincall.order.entity.Status;
import com.backstage.curtaincall.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
        try {
            OrderResponseDto orderResponse = orderService.createOrder(orderRequestDto);
            return ResponseEntity.ok(orderResponse);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 주문 상태 업데이트: 결제 완료
    @PutMapping("/{orderId}/complete")
    public ResponseEntity<String> completeOrder(@PathVariable Long orderId) {
        orderService.updateOrderStatus(orderId, Status.COMPLETED);
        return ResponseEntity.ok("주문이 결제 완료되었습니다.");
    }

    // 주문 상태 업데이트: 결제 실패
    @PutMapping("/{orderId}/fail")
    public ResponseEntity<String> failOrder(@PathVariable Long orderId) {
        orderService.updateOrderStatus(orderId, Status.CANCELED);
        return ResponseEntity.ok("주문이 결제 실패로 처리되었습니다.");
    }

    // 자동 주문 취소 API (프론트에서 5분 초과 시 호출)
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long orderId) {
        orderService.updateOrderStatus(orderId, Status.CANCELED);
        return ResponseEntity.ok("주문이 취소되었습니다.");
    }
}
