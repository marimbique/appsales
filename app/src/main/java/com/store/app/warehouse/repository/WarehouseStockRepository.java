package com.store.app.warehouse.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.store.app.warehouse.entity.WarehouseStock;

public interface WarehouseStockRepository extends JpaRepository<WarehouseStock, Long> {

	// Resolves to stock.product.id; the entity graph fetches the warehouse in the same query, avoiding N+1 when
	// mapping to DTOs.
	@EntityGraph(attributePaths = "warehouse")
	List<WarehouseStock> findByProductId(Long productId);

}
