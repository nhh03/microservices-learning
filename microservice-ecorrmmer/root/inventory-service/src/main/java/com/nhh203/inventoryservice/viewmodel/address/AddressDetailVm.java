package com.nhh203.inventoryservice.viewmodel.address;

import lombok.Builder;

@Builder
public record AddressDetailVm(Long id, String contactName, String phone,
                              String addressLine1, String addressLine2, String city, String zipCode,
                              Long districtId, String districtName, Long stateOrProvinceId,
                              String stateOrProvinceName, Long countryId, String countryName) {

}