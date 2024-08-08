package com.nhh203.inventoryservice.service;


import com.nhh203.inventoryservice.exception.DuplicatedException;
import com.nhh203.inventoryservice.exception.NotFoundException;
import com.nhh203.inventoryservice.model.Warehouse;
import com.nhh203.inventoryservice.repository.StockRepository;
import com.nhh203.inventoryservice.repository.WarehouseRepository;
import com.nhh203.inventoryservice.utils.MessageCode;
import com.nhh203.inventoryservice.viewmodel.AddressVm;
import com.nhh203.inventoryservice.viewmodel.address.AddressDetailVm;
import com.nhh203.inventoryservice.viewmodel.address.AddressPostVm;
import com.nhh203.inventoryservice.viewmodel.warehouse.WarehouseDetailVm;
import com.nhh203.inventoryservice.viewmodel.warehouse.WarehouseGetVm;
import com.nhh203.inventoryservice.viewmodel.warehouse.WarehouseListGetVm;
import com.nhh203.inventoryservice.viewmodel.warehouse.WarehousePostVm;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class WarehouseService {
	private final WarehouseRepository warehouseRepository;
	private final StockRepository stockRepository;
	private final ProductService productService;
	private final LocationService locationService;

	@Transactional(readOnly = true)
	public List<WarehouseGetVm> findAllWarehouses() {
		return warehouseRepository
				.findAll()
				.stream()
				.map(WarehouseGetVm::fromModel)
				.toList();
	}

	public WarehouseDetailVm findById(final Long id) {
		final Warehouse warehouse = warehouseRepository
				.findById(id)
				.orElseThrow(
						() -> new NotFoundException(MessageCode.WAREHOUSE_NOT_FOUND, id));
		AddressDetailVm addressDetailVm = locationService
				.getAddressById(warehouse.getAddressId());
		return WarehouseDetailVm.fromModel(warehouse, addressDetailVm);
	}

	@Transactional
	public Warehouse create(final WarehousePostVm warehousePostVm) {
		if (warehouseRepository.existsByName(warehousePostVm.name())) {
			throw new DuplicatedException(MessageCode.NAME_ALREADY_EXITED, warehousePostVm.name());
		}
		AddressPostVm addressPostVm = new AddressPostVm(
				warehousePostVm.contactName(),
				warehousePostVm.phone(),
				warehousePostVm.addressLine1(),
				warehousePostVm.addressLine2(),
				warehousePostVm.city(),
				warehousePostVm.zipCode(),
				warehousePostVm.districtId(),
				warehousePostVm.stateOrProvinceId(),
				warehousePostVm.countryId()
		);
		AddressVm addressVm = locationService.createAddress(addressPostVm);
		Warehouse warehouse = new Warehouse();
		warehouse.setName(warehousePostVm.name());
		warehouse.setAddressId(addressVm.id());
		return warehouseRepository.save(warehouse);
	}

	@Transactional
	public void update(final WarehousePostVm warehousePostVm, final Long id) {
		Warehouse warehouse = warehouseRepository
				.findById(id)
				.orElseThrow(
						() -> new NotFoundException(MessageCode.WAREHOUSE_NOT_FOUND, id));
		if (warehouseRepository.existsByNameNotUpdatingWarehouse(warehousePostVm.name(), id)) {
			throw new DuplicatedException(MessageCode.NAME_ALREADY_EXITED, warehousePostVm.name());
		}
		AddressPostVm addressPostVm = new AddressPostVm(
				warehousePostVm.contactName(),
				warehousePostVm.phone(),
				warehousePostVm.addressLine1(),
				warehousePostVm.addressLine2(),
				warehousePostVm.city(),
				warehousePostVm.zipCode(),
				warehousePostVm.districtId(),
				warehousePostVm.stateOrProvinceId(),
				warehousePostVm.countryId()
		);
		locationService.updateAddress(warehouse.getAddressId(), addressPostVm);
		warehouse.setName(warehousePostVm.name());
		warehouseRepository.save(warehouse);
	}

	@Transactional
	public void delete(final Long id) {
		final Warehouse warehouse = warehouseRepository
				.findById(id)
				.orElseThrow(() -> new NotFoundException(MessageCode.WAREHOUSE_NOT_FOUND, id));

		warehouseRepository.deleteById(id);
		locationService.deleteAddress(warehouse.getAddressId());
	}

	@Transactional(readOnly = true)
	public WarehouseListGetVm getPageableWarehouses(final int pageNo, final int pageSize) {
		final Pageable pageable = PageRequest.of(pageNo, pageSize);
		final Page<Warehouse> warehousePage = warehouseRepository.findAll(pageable);
		final List<Warehouse> warehouseList = warehousePage.getContent();

		final List<WarehouseGetVm> warehouseVms = warehouseList.stream()
				.map(WarehouseGetVm::fromModel)
				.toList();

		return new WarehouseListGetVm(
				warehouseVms,
				warehousePage.getNumber(),
				warehousePage.getSize(),
				(int) warehousePage.getTotalElements(),
				warehousePage.getTotalPages(),
				warehousePage.isLast()
		);
	}


}
