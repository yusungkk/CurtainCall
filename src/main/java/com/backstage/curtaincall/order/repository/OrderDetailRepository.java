package com.backstage.curtaincall.order.repository;

import com.backstage.curtaincall.order.entity.Order;
import com.backstage.curtaincall.order.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    List<OrderDetail> findByProductDetail_ProductDetailId(Long productDetailId);

    void deleteAllByOrder(Order order);

    @Query("SELECT o.seat FROM OrderDetail o WHERE o.productDetail.id = :productDetailId AND o.seat IN :selectedSeats")
    List<String> findReservedSeats(Long productDetailId, List<String> selectedSeats);

    List<OrderDetail> findAllByOrder(Order order);
}

