package com.store.app.warehouse.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.store.app.exceptions.ProductNotFoundException;
import com.store.app.product.repository.ProductRepository;
import com.store.app.warehouse.dto.WarehouseStockResponse;
import com.store.app.warehouse.repository.WarehouseStockRepository;

@Service 
public class WarehouseStockService {

     private final WarehouseStockRepository stockRepository;
    private final ProductRepository productRepository;

    public WarehouseStockService(WarehouseStockRepository stockRepository, ProductRepository productRepository) {
        this.stockRepository = stockRepository;
        this.productRepository = productRepository;
    }

    public List<WarehouseStockResponse> checkStockByProductId(long productId) {

        if (!productRepository.existsById(productId)) {
            throw new ProductNotFoundException(productId);
        }

        return stockRepository.findByProductId(productId).stream()
                .map(stock -> toWarehouseStockResponse(
                        stock.getWarehouse().getId(),
                        stock.getWarehouse().getName(),
                        stock.getWarehouse().getCity(),
                        stock.getProduct().getId(),
                        stock.getQuantity()
                ))
                .toList();
    }

    public WarehouseStockResponse toWarehouseStockResponse(Long warehouseId, String warehouseName, String warehouseCity, Long productId, int quantity) {

        return new WarehouseStockResponse(warehouseId, warehouseName, warehouseCity, productId, quantity);
    }

   

}
