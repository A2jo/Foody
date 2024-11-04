package com.my.foody.domain.store.entity;

import com.my.foody.domain.base.BaseEntity;
import com.my.foody.domain.owner.entity.Owner;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Store extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, unique = true, nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private Owner owner;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 15, nullable = false)
    private String contact;

    @Column(nullable = false)
    private Long minOrderAmount;

    @Column(nullable = false)
    private LocalTime openTime; //18:00

    @Column(nullable = false)
    private LocalTime endTime;

    @Column(nullable = false)
    private Boolean isDeleted; //삭제되면 true, 운영 중이면 false
}
