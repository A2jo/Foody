package com.my.foody.domain.order.repo;

import com.my.foody.domain.order.entity.Order;
import com.my.foody.domain.orderMenu.repo.dto.OrderProjectionDto;
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


    @Query("select new com.my.foody.domain.orderMenu.repo.dto.OrderProjectionDto(" +
            "s.name, o.orderStatus, a.roadAddress, a.detailedAddress, o.totalAmount, " +
            "GROUP_CONCAT(m.menuName SEPARATOR,', '), o.id, o.createdAt) " +
            "from Order o " +
            "join o.user u " +
            "join o.store s " +
            "join o.address a " +
            "join o.orderMenus om " +
            "join om.menu m " +
            "where o.id = :userId " +
            "group by o.id")
    Page<OrderProjectionDto> findByUserId(@Param("userId") Long userId, Pageable pageable);

}
