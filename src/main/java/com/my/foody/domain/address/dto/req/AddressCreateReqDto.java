package com.my.foody.domain.address.dto.req;

import com.my.foody.domain.address.entity.Address;
import com.my.foody.domain.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@NoArgsConstructor
@Getter
@AllArgsConstructor
@Builder
public class AddressCreateReqDto {

    @NotBlank(message = "도로명 주소를 입력해야 합니다")
    @Length(min = 1, max = 100, message = "최소 1자에서 최대 100자 사이여야 합니다")
    private String roadAddress;

    @NotBlank(message = "상세 주소를 입력해야 합니다")
    @Length(min = 1, max = 30, message = "최소 1자에서 최대 30자 사이여야 합니다")
    private String detailedAddress;

    public Address toEntity(User user){
        return Address.builder()
                .user(user)
                .detailedAddress(detailedAddress)
                .roadAddress(roadAddress)
                .build();
    }
}
