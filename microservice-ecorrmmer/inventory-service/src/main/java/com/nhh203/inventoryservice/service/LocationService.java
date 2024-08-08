package com.nhh203.inventoryservice.service;


import com.nhh203.inventoryservice.config.ServiceUrlConfig;
import com.nhh203.inventoryservice.viewmodel.AddressVm;
import com.nhh203.inventoryservice.viewmodel.address.AddressDetailVm;
import com.nhh203.inventoryservice.viewmodel.address.AddressPostVm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class LocationService {
	private final RestClient restClient;
	private final ServiceUrlConfig serviceUrlConfig;

	public AddressDetailVm getAddressById(Long id) {
		return null;
	}
	public AddressVm createAddress(AddressPostVm addressPostVm) {
		return null;
	}

	public void updateAddress(Long id, AddressPostVm addressPostVm) {

	}
	public void deleteAddress(Long addressId) {

	}

}
