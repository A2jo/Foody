package com.my.foody.domain.address.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@NoArgsConstructor
@Getter
public class AddressModifyReqDto {

    @Length(min = 1, max = 100, message = "최소 1자에서 최대 100자 사이여야 합니다")
    private String roadAddress;

    @Length(min = 1, max = 30, message = "최소 1자에서 최대 30자 사이여야 합니다")
    private String detailedAddress;
}
