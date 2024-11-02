package com.my.foody.domain.user.entity;

import com.my.foody.domain.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
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
    @Column(length = 100, nullable = false)
    private String nickname;
    @Column(length = 60)
    private String password;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(length = 15, nullable = false)
    private String contact;
    @Column(nullable = false)
    private Boolean isDeleted;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider authProvider; //회원가입 방식을 구분
}
