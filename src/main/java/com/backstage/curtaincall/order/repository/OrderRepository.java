package com.backstage.curtaincall.order.repository;

import com.backstage.curtaincall.order.entity.Order;
import com.backstage.curtaincall.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);

    Optional<Order> findByOrderNo(String orderNo);
}
