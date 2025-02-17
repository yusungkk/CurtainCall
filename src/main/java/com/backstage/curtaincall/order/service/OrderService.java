package com.backstage.curtaincall.order.service;

import com.backstage.curtaincall.global.exception.CustomErrorCode;
import com.backstage.curtaincall.global.exception.CustomException;
import com.backstage.curtaincall.order.dto.OrderDetailRequestDto;
import com.backstage.curtaincall.order.dto.OrderRequestDto;
import com.backstage.curtaincall.order.dto.OrderResponseDto;
import com.backstage.curtaincall.order.entity.Order;
import com.backstage.curtaincall.order.entity.OrderDetail;
import com.backstage.curtaincall.order.repository.OrderDetailRepository;
import com.backstage.curtaincall.order.repository.OrderRepository;
import com.backstage.curtaincall.product.entity.ProductDetail;
import com.backstage.curtaincall.product.repository.ProductDetailRepository;
import com.backstage.curtaincall.user.entity.User;
import com.backstage.curtaincall.user.repository.UserRepository;
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

    // 특정 공연 일정(productDetailId)의 예약된 좌석 조회
    public List<String> getReservedSeats(Long productDetailId) {
        return orderDetailRepository.findByProductDetail_ProductDetailId(productDetailId)
                .stream()
                .map(OrderDetail::getSeat) // 예약된 좌석 번호만 추출
                .collect(Collectors.toList());

    }

    // 주문 생성
    public OrderResponseDto createOrder(OrderRequestDto requestDto) {
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
}
