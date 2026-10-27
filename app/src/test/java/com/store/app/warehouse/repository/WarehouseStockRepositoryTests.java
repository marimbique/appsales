package com.store.app.warehouse.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import com.store.app.product.entity.Manufacturer;
import com.store.app.product.entity.Product;
import com.store.app.product.entity.ProductCategory;
import com.store.app.product.repository.ManufacturerRepository;
import com.store.app.product.repository.ProductCategoryRepository;
import com.store.app.product.repository.ProductRepository;
import com.store.app.warehouse.entity.Warehouse;
import com.store.app.warehouse.entity.WarehouseStock;

@DataJpaTest
class WarehouseStockRepositoryTests {

	@Autowired
	private WarehouseStockRepository stockRepository;

	@Autowired
	private WarehouseRepository warehouseRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private ManufacturerRepository manufacturerRepository;

	@Autowired
	private ProductCategoryRepository categoryRepository;

	private Manufacturer manufacturer;
	private ProductCategory category;

	@BeforeEach 
	void setUp() {
		manufacturer = new Manufacturer();
		manufacturer.setName("Acme");
		manufacturer = manufacturerRepository.save(manufacturer);

		category = new ProductCategory();
		category.setName("Laptops");
		category = categoryRepository.save(category);	
	}

	

	@Test
	void findByProductIdReturnsStockAcrossAllWarehouses() {
		Product laptop = saveProduct("SKU-1");
		Warehouse whLisbon = saveWareHouse("Lisbon DC", "Lisbon");
		Warehouse whPorto = saveWareHouse("Porto DC", "Porto");
		saveStock(whPorto, laptop, 120);
		saveStock(whLisbon, laptop, 30);
		assertThat(stockRepository.findByProductId(laptop.getId()))
		.extracting(stock -> stock.getWarehouse().getName())
		.containsExactlyInAnyOrder("Lisbon DC", "Porto DC");

	}

	@Test
	void findByProductIdExcludesOtherProducts() {
		Product laptop = saveProduct("SKU-1");
		Product mouse = saveProduct("SKU-2");
		Warehouse whLisbon = saveWareHouse("Lisbon DC", "Lisbon");
		saveStock(whLisbon, laptop, 120);
		saveStock(whLisbon, mouse, 500);

		assertThat(stockRepository.findByProductId(laptop.getId()))
		.extracting(WarehouseStock::getQuantity)
		.containsExactly(120);
	}

	@Test
	void findByProductIdReturnsEmptyListWhenProductHasNoStock() {
		assertThat(stockRepository.findByProductId(0L)).isEmpty();
	}

	@Test
	void rejectsDuplicateWarehouseAndProduct() {
		Warehouse whLisbon = saveWareHouse("Lisbon DC", "Lisbon");
		Product laptop = saveProduct("SKU-1");
		saveStock(whLisbon, laptop, 120);

		WarehouseStock duplicate = new WarehouseStock();
		duplicate.setWarehouse(whLisbon);
		duplicate.setProduct(laptop);
		duplicate.setQuantity(30);

		assertThatThrownBy(() -> stockRepository.saveAndFlush(duplicate))
			.isInstanceOf(DataIntegrityViolationException.class);
	}

	public WarehouseStock saveStock(Warehouse warehouse, Product product, int quantity) {
		WarehouseStock stock = new WarehouseStock();
		stock.setWarehouse(warehouse);
		stock.setProduct(product);
		stock.setQuantity(quantity);
		return stockRepository.save(stock);
	}

	private Product saveProduct(String sku) {
	Product product = new Product();
	product.setSku(sku);
	product.setName("Laptop");
	product.setPrice(new BigDecimal("10.00"));
	product.setManufacturer(manufacturer);
	product.setCategory(category);
	return productRepository.save(product);
}

	private Warehouse saveWareHouse( String name, String city){
		Warehouse warehouse = new Warehouse();
		warehouse.setName(name);
		warehouse.setCity(city);
		return warehouseRepository.save(warehouse);
	}

}
