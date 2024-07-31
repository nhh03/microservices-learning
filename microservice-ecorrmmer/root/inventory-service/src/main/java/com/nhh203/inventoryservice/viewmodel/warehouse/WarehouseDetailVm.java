package com.nhh203.inventoryservice.viewmodel.warehouse;

import com.nhh203.inventoryservice.model.Warehouse;
import com.nhh203.inventoryservice.viewmodel.address.AddressDetailVm;

public record WarehouseDetailVm(
		Long id,
		String name,
		String contactName,
		String phone,
		String addressLine1,
		String addressLine2,
		String city,
		String zipCode,
		Long districtId,
		Long stateOrProvinceId,
		Long countryId
) {
	public static WarehouseDetailVm fromModel(Warehouse warehouse, AddressDetailVm addressDetailVm) {
		return new WarehouseDetailVm(
				warehouse.getId(),
				warehouse.getName(),
				addressDetailVm.contactName(),
				addressDetailVm.phone(),
				addressDetailVm.addressLine1(),
				addressDetailVm.addressLine2(),
				addressDetailVm.city(),
				addressDetailVm.zipCode(),
				addressDetailVm.districtId(),
				addressDetailVm.stateOrProvinceId(),
				addressDetailVm.countryId()
		);
	}
}
