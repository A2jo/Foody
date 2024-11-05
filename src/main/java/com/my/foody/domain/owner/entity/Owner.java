package com.my.foody.domain.owner.entity;

import com.my.foody.domain.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Table(name = "owner")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
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

    // 정보 업데이트 메서드
    public void updateInfo(String name, String contact, String email, String newPassword) {
        this.name = name;
        this.contact = contact;
        this.email = email;
        this.password = newPassword;
    }

    // 삭제 상태로 변경하는 메서드
    public void markAsDeleted() {
        this.isDeleted = true;
    }
}
