package com.backstage.curtaincall.order.repository;

import com.backstage.curtaincall.order.entity.Order;
import com.backstage.curtaincall.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}
