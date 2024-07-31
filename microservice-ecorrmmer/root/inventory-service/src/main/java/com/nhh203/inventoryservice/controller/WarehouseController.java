package com.nhh203.inventoryservice.controller;


import com.nhh203.inventoryservice.constants.ApiConstant;
import com.nhh203.inventoryservice.model.Warehouse;
import com.nhh203.inventoryservice.viewmodel.error.ErrorVm;
import com.nhh203.inventoryservice.viewmodel.warehouse.WarehouseGetVm;
import com.nhh203.inventoryservice.viewmodel.warehouse.WarehousePostVm;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.nhh203.inventoryservice.service.WarehouseService;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping(ApiConstant.WAREHOUSE_URL)
@RequiredArgsConstructor
public class WarehouseController {
	private final WarehouseService warehouseService;
	@PostMapping
	@ApiResponses(value = {
			@ApiResponse(responseCode = ApiConstant.CODE_201, description = ApiConstant.CREATED, content = @Content(schema = @Schema(implementation = WarehouseGetVm.class))),
			@ApiResponse(responseCode = ApiConstant.CODE_400, description = ApiConstant.BAD_REQUEST, content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
	public ResponseEntity<WarehouseGetVm> createWarehouse(
			@Valid @RequestBody final WarehousePostVm warehousePostVm,
			final UriComponentsBuilder uriComponentsBuilder) {
		final Warehouse warehouse = warehouseService.create(warehousePostVm);
		return ResponseEntity.created(
						uriComponentsBuilder
								.replacePath("/warehouses/{id}")
								.buildAndExpand(warehouse.getId())
								.toUri())
				.body(WarehouseGetVm.fromModel(warehouse));
	}
}
