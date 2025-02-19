package com.backstage.curtaincall.order.service;

import com.backstage.curtaincall.global.exception.CustomErrorCode;
import com.backstage.curtaincall.global.exception.CustomException;
import com.backstage.curtaincall.order.dto.OrderDetailRequestDto;
import com.backstage.curtaincall.order.dto.OrderRequestDto;
import com.backstage.curtaincall.order.dto.OrderResponseDto;
import com.backstage.curtaincall.order.entity.Order;
import com.backstage.curtaincall.order.entity.OrderDetail;
import com.backstage.curtaincall.order.entity.Status;
import com.backstage.curtaincall.order.repository.OrderDetailRepository;
import com.backstage.curtaincall.order.repository.OrderRepository;
import com.backstage.curtaincall.product.entity.Product;
import com.backstage.curtaincall.product.entity.ProductDetail;
import com.backstage.curtaincall.product.repository.ProductDetailRepository;
import com.backstage.curtaincall.product.repository.ProductRepository;
import com.backstage.curtaincall.user.entity.User;
import com.backstage.curtaincall.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final UserRepository userRepository;
    private final ProductDetailRepository productDetailRepository;
    private final ProductRepository productRepository;

    // 특정 공연 일정(productDetailId)의 예약된 좌석 조회
    @Transactional
    public List<String> getReservedSeats(Long productDetailId) {
        return orderDetailRepository.findByProductDetail_ProductDetailId(productDetailId)
                .stream()
                .map(OrderDetail::getSeat) // 예약된 좌석 번호만 추출
                .collect(Collectors.toList());
    }

    // 주문 생성
    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto requestDto) {
        // 동시에 결제 페이지에 들어왔을 경우, 결제하기 버튼을 먼저 누른 사용자가 좌석 선점
        List<String> reservedSeats = orderDetailRepository.findReservedSeats(requestDto.getProductDetailId(), requestDto.getSelectedSeats());

        if (!reservedSeats.isEmpty()) {
            throw new CustomException(CustomErrorCode.SEAT_ALREADY_RESERVED);
        }

        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        // Order 엔티티 저장
        Order order = requestDto.toOrder(user);
        orderRepository.save(order);

        List<OrderDetail> orderDetails = requestDto.getSelectedSeats().stream()
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
        order.setStatus(status);


        if(status.equals(Status.CANCELED)){ // 결제를 취소한 경우, 좌석을 다시 선택할 수 있도록 OrderDetail 삭제
            orderDetailRepository.deleteAllByOrder(order);
        }
        else if (status.equals(Status.COMPLETED)) { // 결제가 완료된 경우, 좌석 수 만큼 상품 판매량 증가
            List<OrderDetail> orderDetails = orderDetailRepository.findAllByOrder(order);

            for (OrderDetail orderDetail : orderDetails) {
                ProductDetail productDetail = orderDetail.getProductDetail();
                Product product = productDetail.getProduct();

                product.setSalesCount(product.getSalesCount() + 1);

                productRepository.save(product);
            }
        }

    }



}
