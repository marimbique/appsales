package com.store.app.warehouse.dto;

public record WarehouseStockResponse(

		Long warehouseId,

		String warehouseName,

		String warehouseCity,

		Long productId,

		int quantity) {

}
