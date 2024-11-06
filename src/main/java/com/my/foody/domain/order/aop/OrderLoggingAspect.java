package com.my.foody.domain.order.aop;

import com.my.foody.domain.order.dto.req.OrderCreateReqDto;
import com.my.foody.domain.order.entity.Order;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
public class OrderLoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(OrderLoggingAspect.class);

    // OrderService의 createOrder() method와 일치하는 Pointcut
    @Pointcut("execution(* com.my.foody.domain.order.service.OrderService.createOrder(..))")
    public void createOrderMethod() {}

    // Before advice to log: 주문이 생성되기 전에 요청 시간, 매장 ID 및 사용자 ID를 기록
    @Before("createOrderMethod() && args(storeId, cartId, orderCreateReqDto, userId)")
    public void logBeforeOrderCreation(Long storeId, Long cartId, OrderCreateReqDto orderCreateReqDto, Long userId) {
        logger.info("주문 생성 요청이 수신: {}, 가계 ID: {}, User ID: {}",
                LocalDateTime.now(), storeId, userId);
    }

    // AfterReturning advice to log: 주문이 성공적으로 생성된 후 주문 생성 및 주문 ID
    @AfterReturning(value = "createOrderMethod() && args(storeId, cartId, orderCreateReqDto, userId)", returning = "order")
    public void logAfterOrderCreation(Order order, Long storeId, Long cartId, OrderCreateReqDto orderCreateReqDto, Long userId) {
        logger.info("주문이 성공적으로 생성되었습니다: {}, 가계 ID: {}, 주문 ID: {}",
                LocalDateTime.now(), storeId, order.getId());
    }
}
