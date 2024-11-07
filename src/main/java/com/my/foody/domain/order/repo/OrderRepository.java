package com.my.foody.domain.order.repo;

import com.my.foody.domain.order.entity.Order;
import com.my.foody.domain.orderMenu.repo.dto.OrderProjectionDto;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("select distinct o from Order o " +
            "left join fetch o.user u " +
            "left join fetch o.store s " +
            "left join fetch o.address a " +
            "where o.id = :orderId")
    Optional<Order> findOrderWithDetails(@Param("orderId") Long orderId);

    @Query(value = "SELECT s.name AS storeName, o.order_status AS orderStatus, " +
            "a.road_address AS roadAddress, a.detailed_address AS detailedAddress, " +
            "o.total_amount AS totalAmount, " +
            "GROUP_CONCAT(m.name ORDER BY m.name SEPARATOR ', ') AS menuNames, " +
            "o.id AS orderId, o.created_at AS createdAt " +
            "FROM orders o " +
            "JOIN users u ON o.user_id = u.id " +
            "JOIN store s ON o.store_id = s.id " +
            "JOIN address a ON o.address_id = a.id " +
            "JOIN order_menu om ON o.id = om.order_id " +
            "JOIN menu m ON om.menu_id = m.id " +
            "WHERE o.user_id = :userId " +
            "GROUP BY o.id",
            nativeQuery = true)
    Page<OrderProjectionDto> findByUserId(@Param("userId") Long userId, Pageable pageable);
}
