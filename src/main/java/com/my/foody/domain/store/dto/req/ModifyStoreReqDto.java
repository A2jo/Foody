package com.my.foody.domain.store.dto.req;


import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ModifyStoreReqDto {
    @Size(min = 1, max = 20, message = "가게 이름은 최대 20자까지 입력할 수 있습니다.")
    private String name;
    private String description;
    @Size(min = 1, max = 15, message = "전화번호는 최대 15자까지 입력할 수 있습니다.")
    private String contact;
    @Pattern(regexp = "^[0-9]+$", message = "최소 주문금액은 숫자만 입력할 수 있습니다.")
    private Long minOrderAmount;
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):([0-5][0-9])$", message = "오픈시간은 시간으로 입력해주세요. ex) 11:00")
    private LocalTime openTime;
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):([0-5][0-9])$", message = "마감시간은 시간으로 입력해주세요. ex) 23:00")
    private LocalTime endTime;
    @Pattern(regexp = "^[0-9]+$", message = "카테고리에 맞는 숫자로 입력해주세요.")
    private List<Long> categoryIds;
    private Boolean isDeleted;

    public boolean hasNoUpdateData() {
        return name == null &&
                description == null &&
                contact == null &&
                minOrderAmount == null &&
                openTime == null &&
                endTime == null &&
                isDeleted == null;
    }
}
