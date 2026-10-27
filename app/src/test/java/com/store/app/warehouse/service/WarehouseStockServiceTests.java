package com.store.app.warehouse.service;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import com.store.app.exceptions.ProductNotFoundException;
import com.store.app.product.entity.Product;
import com.store.app.product.repository.ProductRepository;
import com.store.app.warehouse.dto.WarehouseStockResponse;
import com.store.app.warehouse.entity.Warehouse;
import com.store.app.warehouse.entity.WarehouseStock;
import com.store.app.warehouse.repository.WarehouseStockRepository;

@ExtendWith(MockitoExtension.class)
class WarehouseStockServiceTests {

	@Mock
	private WarehouseStockRepository stockRepository;

	@Mock
	private ProductRepository productRepository;

	private WarehouseStockService service;

	@BeforeEach
	void setUp() {
		service = new WarehouseStockService(stockRepository, productRepository);
	}

	@Test
	void findByProductIdMapsStockRowsToResponses() {
		when(productRepository.existsById(42L)).thenReturn(true);
		when(stockRepository.findByProductId(42L)).thenReturn(List.of(stock(1L, "Lisbon DC", "Lisbon", 42L, 120)));

		assertThat(service.checkStockByProductId(42L))
			.containsExactly(new WarehouseStockResponse(1L, "Lisbon DC", "Lisbon", 42L, 120));

	}

	@Test
	void findByProductIdReturnsEmptyListWhenProductHasNoStock() {
		when(productRepository.existsById(42L)).thenReturn(true);
		when(stockRepository.findByProductId(42L)).thenReturn(List.of());

		assertThat(service.checkStockByProductId(42L)).isEmpty();
	}

	@Test
	void findByProductIdThrowsNotFoundForUnknownProduct() {

		when(productRepository.existsById(42L)).thenReturn(false);


		assertThatThrownBy(() -> service.checkStockByProductId(42L))
			.isInstanceOf(ProductNotFoundException.class)
			.hasMessage("Product with id 42 not found");

	}

	@Test
	void rejectsNegativeQuantity() {

		WarehouseStock stock = new WarehouseStock();
		
		assertThatThrownBy(() -> stock.setQuantity(-1))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Quantity cannot be negative");
	}

	private static WarehouseStock stock(Long warehouseId, String warehouseName, String city, Long productId,
		int quantity) {
	Warehouse warehouse = new Warehouse();
	warehouse.setId(warehouseId);
	warehouse.setName(warehouseName);
	warehouse.setCity(city);

	Product product = new Product();
	product.setId(productId);

	WarehouseStock stock = new WarehouseStock();
	stock.setWarehouse(warehouse);
	stock.setProduct(product);
	stock.setQuantity(quantity);
	return stock;
}


}
