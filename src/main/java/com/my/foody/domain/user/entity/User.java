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

}
