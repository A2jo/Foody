package com.my.foody.domain.order.repo;

import com.my.foody.domain.order.entity.Order;
import jakarta.persistence.SequenceGenerators;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("select distinct o from Order o " +
            "left join fetch o.user u " +
            "left join fetch o.store s " +
            "left join fetch o.address a " +
            "where o.id = :orderId")
    Optional<Order> findOrderWithDetails(@Param("orderId") Long orderId);
}
