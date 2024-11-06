package com.my.foody.domain.user.entity;

public enum Provider {
    GOOGLE, KAKAO;

    public static Provider fromString(String str) {
        try {
            return Provider.valueOf(str.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("존재하지 않는 provider: " + str);
        }
    }
}
