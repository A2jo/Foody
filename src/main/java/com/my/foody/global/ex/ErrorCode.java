package com.my.foody.global.ex;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요"),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED.value(), "만료된 토큰입니다"),
    INVALID_TOKEN_SIGNATURE(HttpStatus.UNAUTHORIZED.value(), "유효하지 않은 토큰 서명 입니다"),
    INVALID_TOKEN_TYPE(HttpStatus.BAD_REQUEST.value(), "유효하지 않은 JWT 형식입니다"),
    INVALID_TOKEN_FORMAT(HttpStatus.BAD_REQUEST.value(), "잘못된 JWT 토큰입니다"),
    MISSING_BEARER_TOKEN(HttpStatus.UNAUTHORIZED.value(), "Bearer 인증 정보가 올바르지 않습니다"),
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN.value(), "접근 권한이 없습니다"),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT.value(), "이미 사용 중인 이메일입니다"),
    NICKNAME_ALREADY_EXISTS(HttpStatus.CONFLICT.value(), "이미 사용 중인 닉네임입니다"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "존재하지 않는 유저입니다"),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST.value(), "비밀번호가 일치하지 않습니다"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED.value(), "유효하지 않은 인증 정보 입니다"),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "존재하지 않는 주문입니다"),
    STORE_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "존재하지 않는 가게입니다"),
    ADDRESS_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "존재하지 않는 주소지입니다"),
    UNAUTHORIZED_ADDRESS_ACCESS(HttpStatus.UNAUTHORIZED.value(), "해당 주소지에 접근 권한이 없습니다"),
    INVALID_ADDRESS_FORMAT(HttpStatus.BAD_REQUEST.value(), "유효하지 않은 주소 형식입니다"),
    CART_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "존재하지 않는 장바구니입니다."),
    ALREADY_DEACTIVATED_USER(HttpStatus.BAD_REQUEST.value(), "탈퇴한 유저입니다"),
    STORENAME_ALREADY_EXISTS(HttpStatus.CONFLICT.value(), "이미 사용 중인 가게이름입니다"),
    HAVE_FULL_STORE(HttpStatus.CONFLICT.value(), "생성 가능한 가게 수를 초과합니다."),
    OWNER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "OWNER를 찾을 수 없습니다."),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "존재하지 않는 카테고리입니다."),
    ALREADY_LINKED_OAUTH(HttpStatus.BAD_REQUEST.value(), "이미 연동된 소셜 계정입니다"),
    EXPIRED_LINKAGE_TOKEN(HttpStatus.UNAUTHORIZED.value(), "만료된 임시 토큰입니다"),
    SOCIAL_ACCOUNT_NOT_FOUND(HttpStatus.BAD_REQUEST.value(), "연동된 소셜 계정을 찾을 수 없습니다"),
    UNSUPPORTED_OAUTH_PROVIDER(HttpStatus.BAD_REQUEST.value(), "지원하지 않는 Oauth2 입니다"),
    MENU_NOT_FOUND(HttpStatus.BAD_REQUEST.value(), "존재하지 않는 메뉴입니다"),
    MENU_NOT_AVAILABLE(HttpStatus.BAD_REQUEST.value(), "주문할 수 없는 메뉴입니다"),
    MENU_IS_SOLD_OUT(HttpStatus.BAD_REQUEST.value(), "품절된 메뉴입니다"),
    MAIN_ADDRESS_NOT_FOUND(HttpStatus.BAD_REQUEST.value(), "기본 주소지가 등록되지 않았습니다"),
    CART_IS_EMPTY(HttpStatus.BAD_REQUEST.value(), "장바구니가 비어있습니다"),
    OAUTH_CALLBACK_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "소셜 인증 서버와 통신 중 오류가 발생했습니다"),
    NO_UPDATE_DATA(HttpStatus.NO_CONTENT.value(), "수정된 내용이 존재하지 않습니다."),
    MENU_NOT_AVAILABLE(HttpStatus.BAD_REQUEST.value(), "현재 주문할 수 없는 메뉴입니다"),
    STORE_NOT_FOUND_IN_CATEGORY(HttpStatus.NOT_FOUND.value(), "해당 카테고리에 해당 가게가 존재하지 않습니다.")
    ;


    private final int status;
    private final String msg;

    ErrorCode(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }
}
