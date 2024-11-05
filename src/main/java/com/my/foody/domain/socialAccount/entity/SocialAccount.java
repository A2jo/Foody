package com.my.foody.domain.socialAccount.entity;

import com.my.foody.domain.base.BaseEntity;
import com.my.foody.domain.user.entity.Provider;
import com.my.foody.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_social_user_provider_id",
                columnNames = {"provider", "providerId"}
        )
})
@AllArgsConstructor
@Builder
public class SocialAccount extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Provider provider; //회원가입 방식을 구분

    private String providerId;

    @Column(nullable = false)
    private boolean isPrimary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 127)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    private String nickname;

}
