package com.my.foody.domain.review.service;

import com.my.foody.domain.address.entity.Address;
import com.my.foody.domain.menu.dto.req.MenuCreateReqDto;
import com.my.foody.domain.order.entity.Order;
import com.my.foody.domain.order.repo.OrderRepository;
import com.my.foody.domain.owner.entity.OrderStatus;
import com.my.foody.domain.owner.entity.Owner;
import com.my.foody.domain.review.dto.req.ReviewCreateReqDto;
import com.my.foody.domain.review.dto.resp.ReviewCreateRespDto;
import com.my.foody.domain.review.entity.Review;
import com.my.foody.domain.review.repo.ReviewRepository;
import com.my.foody.domain.store.entity.Store;
import com.my.foody.domain.store.repo.StoreRepository;
import com.my.foody.domain.store.service.StoreService;
import com.my.foody.domain.user.entity.User;
import com.my.foody.global.ex.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


public class ReviewCreateServiceTest {

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private StoreService storeService;


    private Owner owner;
    private Store store;
    private User user;
    private Order order;
    private Review review;
    private Address address;
    private MenuCreateReqDto menuCreateReqDto;
    private ReviewCreateReqDto requestDto;

    @BeforeEach
    public void setUp() {
        // Mockito 초기화
        MockitoAnnotations.openMocks(this);

        // 유저 생성
        user = User.builder()
                .id(1L)
                .name("테스트이름")
                .nickname("테스트닉네임")
                .password("password123")
                .email("owner@example.com")
                .contact("010-1234-5678")
                .isDeleted(false)
                .build();

        // 주소 생성
        address = Address.builder()
                .id(1L)
                .user(user)
                .roadAddress("서울 용산구 한강대로 123")
                .detailedAddress("내배캠파아트 101-1001")
                .build();

        // 가게 생성
        store = Store.builder()
                .id(1L)
                .name("Test Store")
                .owner(owner) // 가게 소유자 지정
                .description("Test Store Description")
                .contact("010-9876-5432")
                .minOrderAmount(10000L)
                .openTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(22, 0))
                .isDeleted(false)
                .build();

        // 주문 생성
        order = Order.builder()
                .id(5L)
                .user(user)
                .store(store)
                .address(address)
                .totalAmount(10000L)
                .orderStatus(OrderStatus.DELIVERED)
                .build();
    }

    @Test
    @DisplayName("리뷰등록_성공")
    public void tesCreateReview_Success() {
        // Given: 주문이 존재하고, 사용자가 일치하는 경우
        Long orderId = 5L;
        Long userId = 1L;
        Integer rating = 5;
        String comment = "맛있어요!";

        // Mockito가 findById로 주문을 찾을 수 있도록 설정
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(reviewRepository.existsByOrderId(orderId)).thenReturn(false);
        when(storeService.findActivateStoreByIdOrFail(orderId)).thenReturn(store);

        // When: 리뷰 생성 메서드 실행
        ReviewCreateReqDto requestDto = new ReviewCreateReqDto(rating, comment);
        ReviewCreateRespDto response = reviewService.createReview(orderId, requestDto, userId);

        // Then: 리뷰가 정상적으로 저장되고, 응답이 반환됨
        assertNotNull(response);
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    @DisplayName("리뷰등록_실패_주문상태가배달완료가아님")
    public void testCreateReview_Fail_OrderStatusNotDelivered() {
        // Given: 주문이 존재하지만, 주문 상태가 배달 완료가 아닌 경우
        Long orderId = 5L;
        Long userId = 1L;
        Integer rating = 5;
        String comment = "맛있어요!";

        // 주문 상태를 'DELIVERED' 아닌 상태
        OrderStatus orderStatus = OrderStatus.PENDING;
        order = Order.builder()
                .id(orderId)
                .user(user)
                .store(store)
                .orderStatus(orderStatus)
                .build();

        // Mockito가 findById로 주문을 찾을 수 있도록 설정
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(reviewRepository.existsByOrderId(orderId)).thenReturn(false);
        when(storeService.findActivateStoreByIdOrFail(order.getStore().getId())).thenReturn(store);

        // 어떤 데이터가 들어오는지 확인
        System.out.println("주문 상태: " + orderStatus);

        // when & then: 예외 발생 검증
        assertThrows(BusinessException.class, () -> {
            reviewService.createReview(orderId, new ReviewCreateReqDto(rating, comment), userId);
        });
    }


    @Test
    @DisplayName("리뷰등록_실패_이미리뷰가존재함")
    public void testCreateReview_Fail_ReviewAlreadyExists() {
        // Given: 주문이 존재하고, 이미 리뷰가 존재하는 경우
        Long orderId = 5L;
        Long userId = 1L;
        Integer rating = 5;
        String comment = "맛있어요!";

        // Mockito가 findById로 주문을 찾을 수 있도록 설정
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(reviewRepository.existsByOrderId(orderId)).thenReturn(true); // 리뷰가 이미 존재함
        when(storeService.findActivateStoreByIdOrFail(order.getStore().getId())).thenReturn(store);

        // when & then: 예외 발생 검증
        assertThrows(BusinessException.class, () -> {
            reviewService.createReview(orderId, new ReviewCreateReqDto(rating, comment), userId);
        });
    }
}
