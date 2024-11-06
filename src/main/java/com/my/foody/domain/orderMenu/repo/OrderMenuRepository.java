package com.my.foody.domain.orderMenu.repo;

import com.my.foody.domain.order.repo.dto.OrderProjectionRespDto;
import com.my.foody.domain.orderMenu.entity.OrderMenu;
import com.my.foody.domain.owner.entity.Owner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderMenuRepository extends JpaRepository<OrderMenu, Long> {

    @Query("""
            select distinct 
                s.name as storeName,
                o.status as orderStatus,
                o.roadAddress as roadAddress,
                o.detailedAddress as detailedAddress,
                o.totalAmount as totalAmount,
                o.id as orderId,
                o.createdAt as createdAt,
                (select group_concat(m.name)
                 from OrderMenu om2 
                 join Menu m on m.id = om2.menuId
                 where om2.order = o) as menuNames
            from OrderMenu om
            join om.order o
            join o.store s
            where s.owner = :owner
       """)
    Page<OrderProjectionRespDto> findByOwnerWithOrderWithStoreWithMenu(@Param("owner") Owner owner, Pageable pageable);
}
