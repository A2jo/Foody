package com.my.foody.domain.address.entity;

import com.my.foody.domain.base.BaseEntity;
import com.my.foody.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "address")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(length = 100, nullable = false)
    private String roadAddress;

    @Column(length = 30, nullable = false)
    private String detailedAddress;

    @Builder
    public Address(Long id, User user, String roadAddress, String detailedAddress) {
        this.id = id;
        this.user = user;
        this.roadAddress = roadAddress;
        this.detailedAddress = detailedAddress;
    }
}
