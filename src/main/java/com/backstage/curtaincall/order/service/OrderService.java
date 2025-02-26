package com.backstage.curtaincall.order.service;

import com.backstage.curtaincall.global.exception.CustomErrorCode;
import com.backstage.curtaincall.global.exception.CustomException;
import com.backstage.curtaincall.order.dto.*;
import com.backstage.curtaincall.order.entity.Order;
import com.backstage.curtaincall.order.entity.OrderDetail;
import com.backstage.curtaincall.order.entity.Status;
import com.backstage.curtaincall.order.repository.OrderDetailRepository;
import com.backstage.curtaincall.order.repository.OrderRepository;
import com.backstage.curtaincall.payment.entity.Payment;
import com.backstage.curtaincall.payment.repository.PaymentRepository;
import com.backstage.curtaincall.payment.service.PaymentService;
import com.backstage.curtaincall.product.entity.Product;
import com.backstage.curtaincall.product.entity.ProductDetail;
import com.backstage.curtaincall.product.entity.Time;
import com.backstage.curtaincall.product.repository.ProductDetailRepository;
import com.backstage.curtaincall.product.repository.ProductRepository;
import com.backstage.curtaincall.user.entity.User;
import com.backstage.curtaincall.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.backstage.curtaincall.order.entity.Status.*;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final UserRepository userRepository;
    private final ProductDetailRepository productDetailRepository;
    private final ProductRepository productRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;

    // 특정 공연 일정(productDetailId)의 예약된 좌석 조회
    @Transactional(readOnly = true)
    public List<String> getReservedSeats(Long productDetailId) {
        return orderDetailRepository.findByProductDetail_ProductDetailId(productDetailId)
                .stream()
                .map(OrderDetail::getSeat) // 예약된 좌석 번호만 추출
                .collect(Collectors.toList());
    }

    // 주문 성공 응답 반환
    @Transactional(readOnly = true)
    public OrderSuccessResponseDto getOrderSuccess(Long orderId) {
        // 성공된 주문 찾기
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        Order findOrder = optionalOrder.orElseThrow(() -> new CustomException(CustomErrorCode.ORDER_NOT_FOUND));

        // 성공된 주문의 결제 정보
        Optional<Payment> optionalPayment = paymentRepository.findByOrder(findOrder);
        Payment findPayment = optionalPayment.orElseThrow(() -> new CustomException(CustomErrorCode.PAYMENT_NOT_FOUND));

        // 성공된 주문의 좌석 정보
        List<String> orderSeats = new ArrayList<>();
        List<OrderDetail> orderDetails = findOrder.getOrderDetails();
        for (OrderDetail orderDetail : orderDetails) {
            orderSeats.add(orderDetail.getSeat());
        }

        // 성공된 주문의 상품 상세
        ProductDetail productDetail = orderDetails.get(0).getProductDetail();
        LocalDate performanceDate = productDetail.getPerformanceDate();
        Time performanceTime = productDetail.getTime();

        // 성공된 주문의 상품
        Product product = productDetail.getProduct();
        String productName = product.getProductName();
        String place = product.getPlace();
        String imageUrl = product.getProductImage().getImageUrl();

        return OrderSuccessResponseDto.create(findOrder.getOrderNo(), findPayment.getPrice(), orderSeats, productName, place, performanceDate, performanceTime, imageUrl);
    }

    // 주문 내역 조회
    @Transactional(readOnly = true)
    public List<OrderHistoryDto> getOrderHistory(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        User findUser = optionalUser.orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        List<OrderHistoryDto> orderHistories = new ArrayList<>();
        // 유저의 주문 내역
        List<Order> findOrders = orderRepository.findByUser(findUser);
        if (!findOrders.isEmpty()) {
            for (Order order : findOrders) {
                Status status = order.getStatus();
                if (status.equals(CANCELED) || status.equals(PENDING) || status.equals(REFUNDED)) {
                    continue;
                }
                // 주문의 주문 상세
                List<OrderDetail> orderDetails = order.getOrderDetails();

                // 주문의 선택 좌석
                List<String> orderSeats = new ArrayList<>();
                for (OrderDetail orderDetail : orderDetails) {
                    orderSeats.add(orderDetail.getSeat());
                }

                ProductDetail productDetail = orderDetails.get(0).getProductDetail();
                LocalDate performanceDate = productDetail.getPerformanceDate();
                Time performanceTime = productDetail.getTime();

                Product product = productDetail.getProduct();
                String productName = product.getProductName();
                String imageUrl = product.getProductImage().getImageUrl();

                OrderHistoryDto orderHistoryDto = OrderHistoryDto.create(order.getOrderNo(), order.getPrice(), orderSeats, performanceDate, performanceTime, productName, imageUrl);
                orderHistories.add(orderHistoryDto);
            }
        }

        return orderHistories;
    }

    // 주문 생성
    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto requestDto) {
        List<String> getSelectedSeats = requestDto.getSelectedSeats();
        List<String> reservedSeats = orderDetailRepository.findReservedSeats(requestDto.getProductDetailId(), getSelectedSeats);

        // 동시에 결제 페이지에 들어왔을 경우, 결제하기 버튼을 먼저 누른 사용자가 좌석 선점
        if (!reservedSeats.isEmpty()) {
            throw new CustomException(CustomErrorCode.SEAT_ALREADY_RESERVED);
        }

        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        // Order 엔티티 저장
        Order order = requestDto.toOrder(user);
        orderRepository.save(order);

        List<OrderDetail> orderDetails = getSelectedSeats.stream()
                .map(seat -> {
                    ProductDetail productDetail = productDetailRepository.findById(requestDto.getProductDetailId())
                            .orElseThrow(() -> new CustomException(CustomErrorCode.PRODUCT_NOT_FOUND));
                    return new OrderDetailRequestDto(seat).toOrderDetail(order, productDetail);
                })
                .collect(Collectors.toList());

        orderDetailRepository.saveAll(orderDetails);

        return OrderResponseDto.fromEntity(order, orderDetails);
    }

    // 주문 상태 업데이트
    @Transactional
    public void updateOrderStatus(Long orderId, Status status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.ORDER_NOT_FOUND));
        order.updateStatus(status);


        if(status.equals(CANCELED)){ // 결제를 취소한 경우, 좌석을 다시 선택할 수 있도록 OrderDetail 삭제
            orderDetailRepository.deleteAllByOrder(order);
        }
        else if (status.equals(COMPLETED)) { // 결제가 완료된 경우, 좌석 수 만큼 상품 판매량 증가 & 상품 상세 잔여석 감소
            List<OrderDetail> orderDetails = orderDetailRepository.findAllByOrder(order);

            for (OrderDetail orderDetail : orderDetails) {
                ProductDetail productDetail = orderDetail.getProductDetail();
                Product product = productDetail.getProduct();

                productDetail.updateRemain(productDetail.getRemain() - 1);
                product.updateSalesCount(product.getSalesCount() + 1);

                productRepository.save(product);
            }
        }

    }

    // 주문, 결제 취소
    @Transactional
    public void cancelOrderPayment(String orderNo) {
        Optional<Order> optionalOrder = orderRepository.findByOrderNo(orderNo);
        Order findOrder = optionalOrder.orElseThrow(() -> new CustomException(CustomErrorCode.ORDER_NOT_FOUND));

        // 주문 상태 환불로 변경
        findOrder.updateStatus(REFUNDED);

        List<OrderDetail> orderDetails = orderDetailRepository.findAllByOrder(findOrder);
        for (OrderDetail od : orderDetails) {
            // 잔여석 복구
            ProductDetail productDetail = od.getProductDetail();
            productDetail.updateRemain(productDetail.getRemain() + 1);

            // 판매량 복구
            Product product = productDetail.getProduct();
            product.updateSalesCount(product.getSalesCount() - 1);

            // 주문 상세 삭제 -> 좌석 선택할 수 있도록
            orderDetailRepository.delete(od);
        }

        paymentService.refund(findOrder);
    }
}
