package com.my.foody.domain.order.aop;

import com.my.foody.domain.order.dto.req.OrderCreateReqDto;
import com.my.foody.domain.order.entity.Order;
import org.aspectj.lang.JoinPoint;
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


    @Pointcut("execution(* com.my.foody.domain.order.service.OrderService.createOrder(..))")
    public void createOrderMethod() {}

    @Before("createOrderMethod()")
    public void logBeforeOrderCreation(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Long cartId = (Long) args[0];
        Long userId = (Long) args[1];

        logger.info("주문 생성 요청 수신: 시각: {}, Cart ID: {}, User ID: {}",
                LocalDateTime.now(), cartId, userId);
    }

    @AfterReturning(pointcut = "createOrderMethod()", returning = "order")
    public void logAfterOrderCreation(JoinPoint joinPoint, Order order) {
        logger.info("주문이 성공적으로 생성되었습니다: 시각: {}, Store ID: {}, 주문 ID: {}",
                LocalDateTime.now(), order.getStore().getId(), order.getId());
    }
}
