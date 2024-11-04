package com.my.foody.domain.owner.entity;

import com.my.foody.domain.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Owner extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String name;
    @Column(length = 15)
    private String contact;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(length = 60, nullable = false)
    private String password;
    @Column(nullable = false)
    private Boolean isDeleted;

    @Builder
    public Owner(Long id, String name, String contact, String email, String password, Boolean isDeleted) {
        this.name = name;
        this.contact = contact;
        this.email = email;
        this.password = password;
        this.isDeleted = isDeleted;
    }
}
