package com.my.foody.domain.store.entity;

import com.my.foody.domain.base.BaseEntity;
import com.my.foody.domain.owner.entity.Owner;
import com.my.foody.domain.store.dto.req.ModifyStoreReqDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.Optional;

@Entity
@Getter
@Setter
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

    @Builder
    public Store(Long id, String name, Owner owner, String description, String contact, Long minOrderAmount, LocalTime openTime, LocalTime endTime, boolean isDeleted) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.description = description;
        this.contact = contact;
        this.minOrderAmount = minOrderAmount;
        this.openTime = openTime;
        this.endTime = endTime;
        this.isDeleted = isDeleted;
    }

    public void updateAll(ModifyStoreReqDto modifyStoreReqDto) {
        Optional.ofNullable(modifyStoreReqDto.getName()).ifPresent(name -> this.name = name);
        Optional.ofNullable(modifyStoreReqDto.getDescription()).ifPresent(description -> this.description = description);
        Optional.ofNullable(modifyStoreReqDto.getContact()).ifPresent(contact -> this.contact = contact);
        Optional.ofNullable(modifyStoreReqDto.getMinOrderAmount()).ifPresent(minOrderAmount -> this.minOrderAmount = minOrderAmount);
        Optional.ofNullable(modifyStoreReqDto.getOpenTime()).ifPresent(openTime -> this.openTime = openTime);
        Optional.ofNullable(modifyStoreReqDto.getEndTime()).ifPresent(endTime -> this.endTime = endTime);
        Optional.ofNullable(modifyStoreReqDto.getIsDeleted()).ifPresent(isDeleted -> this.isDeleted = isDeleted);
    }
}
