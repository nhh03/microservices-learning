package com.nhh203.inventoryservice.viewmodel.warehouse;
import com.nhh203.inventoryservice.model.Warehouse;
public record WarehouseGetVm(Long id, String name) {
	public static WarehouseGetVm fromModel(Warehouse warehouse) {
		return new WarehouseGetVm(warehouse.getId(), warehouse.getName());
	}
}
