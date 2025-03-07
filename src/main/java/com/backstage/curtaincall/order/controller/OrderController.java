package com.backstage.curtaincall.order.controller;

import com.backstage.curtaincall.global.exception.CustomException;
import com.backstage.curtaincall.order.dto.OrderHistoryDto;
import com.backstage.curtaincall.order.dto.OrderRequestDto;
import com.backstage.curtaincall.order.dto.OrderResponseDto;
import com.backstage.curtaincall.order.dto.OrderSuccessResponseDto;
import com.backstage.curtaincall.order.entity.Status;
import com.backstage.curtaincall.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 예약된 좌석 조회 API
    @GetMapping("/reserved-seats")
    public ResponseEntity<List<String>> getReservedSeats(@RequestParam Long productDetailId) {
        List<String> reservedSeats = orderService.getReservedSeats(productDetailId);
        return ResponseEntity.ok(reservedSeats);
    }

    // 주문 성공 응답 API
    @GetMapping("/{orderId}/success")
    public ResponseEntity<OrderSuccessResponseDto> getOrderSuccessResponse(@PathVariable Long orderId) {
        // globalExceptionHandler 처리
        OrderSuccessResponseDto response = orderService.getOrderSuccess(orderId);
        return ResponseEntity.ok(response);
    }

    // 주문 내역 조회
    @PostMapping("/history")
    public ResponseEntity<List<OrderHistoryDto>> getOrderList(@RequestBody Map<String, String> request) {
        String email = request.get("email").replace("\"", "");
        List<OrderHistoryDto> responses = orderService.getOrderHistory(email);
        return ResponseEntity.ok(responses);
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

    // 완료된 주문 취소 API (예매 내역에서 취소)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/cancel")
    public void refundOrder(@RequestBody Map<String, String> request) {
        String orderNo = request.get("orderNo").replace("\"", "");
        orderService.cancelOrderPayment(orderNo);
    }
}
