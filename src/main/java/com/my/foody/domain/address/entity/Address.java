package com.my.foody.domain.address.entity;

import com.my.foody.domain.base.BaseEntity;
import com.my.foody.domain.user.entity.User;
import com.my.foody.global.ex.BusinessException;
import com.my.foody.global.ex.ErrorCode;
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

    public void modifyAll(String roadAddress, String detailedAddress){
        validateAddress(roadAddress, detailedAddress);
        this.roadAddress = roadAddress;
        this.detailedAddress = detailedAddress;
    }

    private void validateAddress(String roadAddress, String detailedAddress) {
        if(roadAddress == null || roadAddress.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_ADDRESS_FORMAT);
        }
        if(detailedAddress == null || detailedAddress.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_ADDRESS_FORMAT);
        }
    }


    public void validateUser(User user){
        if(!this.user.getId().equals(user.getId())){
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ADDRESS_ACCESS);
        }
    }
}
