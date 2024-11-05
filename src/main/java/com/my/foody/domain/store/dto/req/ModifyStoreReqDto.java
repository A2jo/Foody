package com.my.foody.domain.store.dto.req;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotBlank(message = "가게 이름를 입력해주세요")
    @Size(min = 1, max = 20, message = "가게 이름은 최대 20자까지 입력할 수 있습니다.")
    private String name;
    @NotBlank(message = "가게 설명을 입력해주세요")
    private String description;
    @NotBlank(message = "가게의 전화번호를 입력해주세요")
    @Size(min=1, max=15, message = "전화번호는 최대 15자까지 입력할 수 있습니다.")
    private String contact;
    @NotBlank(message = "최소 주문금액을 입력해주세요")
    @Pattern(regexp = "^[0-9]+$", message = "최소 주문금액은 숫자만 입력할 수 있습니다.")
    private Long minOrderAmount;
    @NotBlank(message = "가게 오픈시간을 입력해주세요")
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):([0-5][0-9])$", message = "오픈시간은 시간으로 입력해주세요. ex) 11:00")
    private LocalTime openTime;
    @NotBlank(message = "가게 마감시간을 입력해주세요")
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):([0-5][0-9])$", message = "마감시간은 시간으로 입력해주세요. ex) 23:00")
    private LocalTime endTime;
    @NotBlank(message = "가게의 카테고리를 입력해주세요")
    @Pattern(regexp = "^[0-9]+$", message = "카테고리에 맞는 숫자로 입력해주세요.")
    private List<Long> categoryIds;
    @NotNull(message = "가게의 영업상태를 설정해주세요")
    private Boolean isDeleted;

}
