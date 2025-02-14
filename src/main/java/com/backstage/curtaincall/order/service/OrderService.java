package com.backstage.curtaincall.order.service;

import com.backstage.curtaincall.order.entity.OrderDetail;
import com.backstage.curtaincall.order.repository.OrderDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderDetailRepository orderDetailRepository;

    // 특정 공연 일정(productDetailId)의 예약된 좌석 조회
    public List<String> getReservedSeats(Long productDetailId) {
        return orderDetailRepository.findByProductDetail_ProductDetailId(productDetailId)
                .stream()
                .map(OrderDetail::getSeat) // 예약된 좌석 번호만 추출
                .collect(Collectors.toList());

    }
}
