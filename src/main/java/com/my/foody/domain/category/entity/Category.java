package com.my.foody.domain.category.entity;

import com.my.foody.domain.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.*;
import lombok.*;

@Entity
@Getter
@Table(name = "category")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "name"}) // toString 오버라이드, 주요 필드만 포함
public class Category extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 10, nullable = false, unique = true)
    private String name;

    @Builder
    public Category(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Builder
    public Category(String name) {
        this.name = name;
    }

    @Builder
    public Category(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    // 이름을 매개변수로 받는 생성자 추가
    public Category(String name) {
        this.name = name;
    }

    @Builder
    public Category(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Builder
    public Category(String name) {
        this.name = name;
    }

    @Builder
    public Category(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
