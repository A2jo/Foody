package com.my.foody.domain.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.foody.domain.order.dto.resp.OrderPreviewRespDto;
import com.my.foody.domain.order.service.OrderService;
import com.my.foody.global.ex.BusinessException;
import com.my.foody.global.ex.ErrorCode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.my.foody.domain.order.dto.resp.OrderListRespDto;
import com.my.foody.domain.order.repo.dto.OrderProjectionRespDto;
import com.my.foody.domain.order.service.OrderService;
import com.my.foody.domain.owner.entity.OrderStatus;
import com.my.foody.domain.user.controller.UserController;
import com.my.foody.global.jwt.JwtProvider;
import com.my.foody.global.jwt.JwtVo;
import com.my.foody.global.jwt.TokenSubject;
import com.my.foody.global.jwt.UserType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;


    @MockBean
    private OrderService orderService;

    @MockBean
    private JwtProvider jwtProvider;

    @Test
    @DisplayName("주문 미리보기 성공 테스트")
    void getOrderPreview_Success() throws Exception {
        // given
        Long userId = 1L;
        Long storeId = 1L;
        Long cartId = 1L;
        String token = "test.token";

        TokenSubject tokenSubject = new TokenSubject(userId, UserType.USER);
        OrderPreviewRespDto response = OrderPreviewRespDto.builder()
                .storeName("테스트가게")
                .storeId(storeId)
                .userContact("01012345678")
                .roadAddress("서울시 강남구")
                .detailedAddress("123-456")
                .totalAmount(20000L)
                .cartMenuList(List.of(
                        OrderPreviewRespDto.CartMenuRespDto.builder()
                                .menuName("테스트메뉴")
                                .menuPrice(10000L)
                                .quantity(2L)
                                .build()
                ))
                .build();

        when(jwtProvider.validate(token)).thenReturn(tokenSubject);
        when(orderService.getOrderPreview(userId, storeId, cartId))
                .thenReturn(response);

        // when & then
        mvc.perform(get("/api/home/stores/{storeId}/cart/{cartId}/orders", storeId, cartId)
                        .header(JwtVo.HEADER, JwtVo.TOKEN_PREFIX + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.storeName").value("테스트가게"))
                .andExpect(jsonPath("$.data.storeId").value(storeId))
                .andExpect(jsonPath("$.data.userContact").value("01012345678"))
                .andExpect(jsonPath("$.data.roadAddress").value("서울시 강남구"))
                .andExpect(jsonPath("$.data.detailedAddress").value("123-456"))
                .andExpect(jsonPath("$.data.totalAmount").value(20000))
                .andExpect(jsonPath("$.data.cartMenuList[0].menuName").value("테스트메뉴"))
                .andExpect(jsonPath("$.data.cartMenuList[0].menuPrice").value(10000))
                .andExpect(jsonPath("$.data.cartMenuList[0].quantity").value(2));

        verify(orderService).getOrderPreview(userId, storeId, cartId);
    }

    @Test
    @DisplayName("주문 미리보기 실패 테스트 - 존재하지 않는 유저")
    void getOrderPreview_UserNotFound() throws Exception {
        // given
        Long userId = 1L;
        Long storeId = 1L;
        Long cartId = 1L;
        String token = "test.token";

        TokenSubject tokenSubject = new TokenSubject(userId, UserType.USER);

        when(jwtProvider.validate(token)).thenReturn(tokenSubject);
        when(orderService.getOrderPreview(userId, storeId, cartId))
                .thenThrow(new BusinessException(ErrorCode.USER_NOT_FOUND));

        // when & then
        mvc.perform(get("/api/home/stores/{storeId}/cart/{cartId}/orders", storeId, cartId)
                        .header(JwtVo.HEADER, JwtVo.TOKEN_PREFIX + token))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.apiError.msg").value("존재하지 않는 유저입니다"));

        verify(orderService).getOrderPreview(userId, storeId, cartId);
    }

    @Test
    @DisplayName("주문 미리보기 실패 테스트 - 빈 장바구니")
    void getOrderPreview_EmptyCart() throws Exception {
        // given
        Long userId = 1L;
        Long storeId = 1L;
        Long cartId = 1L;
        String token = "test.token";

        TokenSubject tokenSubject = new TokenSubject(userId, UserType.USER);

        when(jwtProvider.validate(token)).thenReturn(tokenSubject);
        when(orderService.getOrderPreview(userId, storeId, cartId))
                .thenThrow(new BusinessException(ErrorCode.CART_IS_EMPTY));

        // when & then
        mvc.perform(get("/api/home/stores/{storeId}/cart/{cartId}/orders", storeId, cartId)
                        .header(JwtVo.HEADER, JwtVo.TOKEN_PREFIX + token))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.apiError.msg").value("장바구니가 비어있습니다"));

        verify(orderService).getOrderPreview(userId, storeId, cartId);
    }

    @Test
    @DisplayName("주문 목록 조회 성공 테스트")
    void getAllOrder_Success() throws Exception {
        // Given
        Long ownerId = 1L;
        int page = 0;
        int limit = 10;
        String token = "test.token";
        TokenSubject tokenSubject = new TokenSubject(ownerId, UserType.OWNER);
        OrderListRespDto mockResponse = createMockOrderListRespDto();

        when(jwtProvider.validate(token)).thenReturn(tokenSubject);
        when(orderService.getAllOrder(ownerId, page, limit)).thenReturn(mockResponse);

        // When & Then
        mvc.perform(get("/api/owners/orders")
                        .param("page", String.valueOf(page))
                        .param("limit", String.valueOf(limit))
                        .header(JwtVo.HEADER, JwtVo.TOKEN_PREFIX + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.orderList").isArray())
                .andExpect(jsonPath("$.data.orderList[0].storeName").value("맛있는 치킨"))
                .andExpect(jsonPath("$.data.orderList[0].menuSummary").value("후라이드 치킨 외 2개"))
                .andExpect(jsonPath("$.data.pageInfo.pageNumber").value(page))
                .andExpect(jsonPath("$.data.pageInfo.pageSize").value(limit))
                .andDo(print());

        verify(orderService).getAllOrder(ownerId, page, limit);
    }

    @Test
    @DisplayName("주문 목록 조회 성공 테스트: 빈 목록 반환")
    void getAllOrder_EmptyList() throws Exception {
        // Given
        Long ownerId = 1L;
        String token = "test.token";
        TokenSubject tokenSubject = new TokenSubject(ownerId, UserType.OWNER);
        OrderListRespDto emptyResponse = createEmptyOrderListRespDto();

        when(jwtProvider.validate(token)).thenReturn(tokenSubject);
        when(orderService.getAllOrder(ownerId, 0, 10)).thenReturn(emptyResponse);

        // When & Then
        mvc.perform(get("/api/owners/orders")
                        .param("page", "0")
                        .param("limit", "10")
                        .header(JwtVo.HEADER, JwtVo.TOKEN_PREFIX + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.orderList").isEmpty())
                .andExpect(jsonPath("$.data.pageInfo.totalElements").value(0))
                .andDo(print());
    }

    private OrderListRespDto createMockOrderListRespDto() {
        List<OrderProjectionRespDto> projections = Arrays.asList(
                createMockProjection("맛있는 치킨", "후라이드 치킨,양념 치킨,콜라", 50000L, 1L),
                createMockProjection("맛있는 피자", "페퍼로니피자", 25000L, 2L)
        );

        Page<OrderProjectionRespDto> page = new PageImpl<>(
                projections,
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt")),
                projections.size()
        );

        return new OrderListRespDto(page);
    }

    private OrderListRespDto createEmptyOrderListRespDto() {
        return new OrderListRespDto(
                new PageImpl<>(
                        Collections.emptyList(),
                        PageRequest.of(0, 10),
                        0
                )
        );
    }

    private OrderProjectionRespDto createMockProjection(
            String storeName, String menuNames, Long totalAmount, Long orderId) {
        return new OrderProjectionRespDto() {
            @Override
            public String getStoreName() { return storeName; }

            @Override
            public OrderStatus getOrderStatus() { return OrderStatus.DELIVERED; }

            @Override
            public String getRoadAddress() { return "서울시 강남구"; }

            @Override
            public String getDetailedAddress() { return "테헤란로 123"; }

            @Override
            public String getMenuNames() { return menuNames; }

            @Override
            public Long getOrderId() { return orderId; }

            @Override
            public Long getTotalAmount() { return totalAmount; }

            @Override
            public LocalDateTime getCreatedAt() { return LocalDateTime.now(); }
        };
    }


}