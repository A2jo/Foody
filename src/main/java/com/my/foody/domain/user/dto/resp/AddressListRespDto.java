package com.my.foody.domain.user.dto.resp;

import com.my.foody.domain.address.entity.Address;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter
public class AddressListRespDto {
    private List<AddressRespDto> addressList;

    public AddressListRespDto(List<Address> addressList) {
        this.addressList = addressList.stream()
                .map(AddressRespDto::new)
                .collect(Collectors.toList());
    }

    @NoArgsConstructor
    @Getter
    public static class AddressRespDto{
        public AddressRespDto(Address address) {
            this.addressId = address.getId();
            this.roadAddress = address.getRoadAddress();
            this.detailedAddress = address.getDetailedAddress();
        }

        private Long addressId;
        private String roadAddress;
        private String detailedAddress;
    }
}
