package com.store.app.warehouse.controller;

import org.springframework.web.bind.annotation.RestController;

import com.store.app.warehouse.dto.WarehouseStockResponse;
import com.store.app.warehouse.service.WarehouseStockService;

import lombok.NonNull;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/stock")
public class WarehouseStockController {

    private WarehouseStockService warehouseStockService;

    public WarehouseStockController(WarehouseStockService warehouseStockService) {
        this.warehouseStockService = warehouseStockService;
    }

    @GetMapping
    public List<WarehouseStockResponse> get(@RequestParam Long productId) {
        return warehouseStockService.checkStockByProductId(productId);
    }
}
