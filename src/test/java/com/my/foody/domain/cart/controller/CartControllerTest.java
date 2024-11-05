package com.my.foody.domain.cart.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.foody.domain.cart.dto.req.CartMenuCreateReqDto;
import com.my.foody.domain.cart.dto.resp.CartMenuCreateRespDto;
import com.my.foody.domain.cart.entity.Cart;
import com.my.foody.domain.cart.service.CartService;
import com.my.foody.domain.cartMenu.CartMenu;
import com.my.foody.domain.menu.entity.Menu;
import com.my.foody.global.jwt.JwtProvider;
import com.my.foody.global.jwt.JwtVo;
import com.my.foody.global.jwt.TokenSubject;
import com.my.foody.global.jwt.UserType;
import com.my.foody.global.util.DummyObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CartController.class)
@AutoConfigureMockMvc
class CartControllerTest extends DummyObject {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CartService cartService;
    @MockBean
    private JwtProvider jwtProvider;

    @Test
    @DisplayName("장바구니에 메뉴 추가 성공 테스트")
    void addCartItem_Success() throws Exception {
        // given
        Long storeId = 1L;
        Long menuId = 1L;
        Long userId = 1L;
        Long cartId = 1L;

        String token = "test.token";
        TokenSubject tokenSubject = new TokenSubject(userId, UserType.USER);
        CartMenuCreateReqDto request = new CartMenuCreateReqDto(2L);
        CartMenuCreateRespDto response = new CartMenuCreateRespDto(
                CartMenu.builder()
                        .cart(Cart.builder().id(cartId).build())
                        .menu(Menu.builder().id(menuId).build())
                        .quantity(2L)
                        .build()
        );

        when(jwtProvider.validate(token)).thenReturn(tokenSubject);
        when(cartService.addCartItem(eq(storeId), eq(menuId), any(CartMenuCreateReqDto.class), eq(userId)))
                .thenReturn(response);

        // when & then
        String result = mockMvc.perform(post("/api/home/stores/{storeId}/menus/{menuId}", storeId, menuId)
                        .header(JwtVo.HEADER, JwtVo.TOKEN_PREFIX + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.cartId").value(cartId))
                .andExpect(jsonPath("$.data.menuId").value(menuId))
                .andExpect(jsonPath("$.data.quantity").value(2))
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    @DisplayName("장바구니에 메뉴 추가 실패 테스트:  잘못된 수량")
    void addCartItem_InvalidQuantity() throws Exception {
        // given
        Long storeId = 1L;
        Long menuId = 1L;
        Long userId = 1L;
        String token = "test.token";
        TokenSubject tokenSubject = new TokenSubject(userId, UserType.USER);
        CartMenuCreateReqDto request = new CartMenuCreateReqDto(-1L);

        when(jwtProvider.validate(token)).thenReturn(tokenSubject);

        // when & then
        mockMvc.perform(post("/api/home/stores/{storeId}/menus/{menuId}", storeId, menuId)
                        .header(JwtVo.HEADER, JwtVo.TOKEN_PREFIX + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .requestAttr("tokenSubject", tokenSubject))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.apiError").exists());

        verify(cartService, never()).addCartItem(anyLong(), anyLong(), any(), anyLong());
    }


}