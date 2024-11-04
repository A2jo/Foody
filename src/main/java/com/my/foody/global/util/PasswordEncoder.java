package com.my.foody.global.util;

import org.springframework.security.crypto.bcrypt.BCrypt;

public class PasswordEncoder {
    // 비밀번호 해싱
    public static String encode(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }

    //비밀번호 검증
    public static boolean matches(String plainTextPassword, String hashedPassword) {
        if (hashedPassword == null || !hashedPassword.startsWith("$2a$")) {
            return false;
        }
        return BCrypt.checkpw(plainTextPassword, hashedPassword);
    }
}
