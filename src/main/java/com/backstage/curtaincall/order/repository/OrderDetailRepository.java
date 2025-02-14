package com.backstage.curtaincall.order.repository;

import com.backstage.curtaincall.order.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    List<OrderDetail> findByProductDetail_ProductDetailId(Long productDetailId);
}

