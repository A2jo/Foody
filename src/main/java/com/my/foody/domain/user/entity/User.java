package com.my.foody.domain.user.entity;

import com.my.foody.domain.base.BaseEntity;
import com.my.foody.global.ex.BusinessException;
import com.my.foody.global.ex.ErrorCode;
import com.my.foody.global.util.PasswordEncoder;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 100, nullable = false)
    private String name;
    @Column(unique = true, length = 100)
    private String nickname;
    @Column(length = 60)
    private String password;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(length = 15)
    private String contact;
    @Column(nullable = false)
    private Boolean isDeleted;

    @Builder
    public User(Long id, String name, String nickname, String password, String email, String contact, Boolean isDeleted) {
        this.id = id;
        this.name = name;
        this.nickname = nickname;
        this.password = password;
        this.email = email;
        this.contact = contact;
        this.isDeleted = isDeleted;
    }

    public void matchPassword(String password){
        if(!PasswordEncoder.matches(password, this.password)){
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }
    }
}
