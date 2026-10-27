package com.store.app.warehouse;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import com.store.app.product.entity.Manufacturer;
import com.store.app.product.entity.Product;
import com.store.app.product.entity.ProductCategory;
import com.store.app.product.repository.ManufacturerRepository;
import com.store.app.product.repository.ProductCategoryRepository;
import com.store.app.product.repository.ProductRepository;
import com.store.app.warehouse.entity.Warehouse;
import com.store.app.warehouse.entity.WarehouseStock;
import com.store.app.warehouse.repository.WarehouseRepository;
import com.store.app.warehouse.repository.WarehouseStockRepository;

@SpringBootTest
@AutoConfigureMockMvc
class WarehouseStockControllerTests {

	@Autowired
	private MockMvc mockMvc;

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
		deleteAll();

		manufacturer = new Manufacturer();
		manufacturer.setName("Acme");
		manufacturer = manufacturerRepository.save(manufacturer);

		category = new ProductCategory();
		category.setName("Laptops");
		category = categoryRepository.save(category);
	}

	// @SpringBootTest does not roll back, and the database is shared with the other test classes,
	// so leftover stock rows would block ProductControllerTests from deleting products
	@AfterEach
	void tearDown() {
		deleteAll();
	}

	@Test
	void returnsStockPerWarehouseForProduct() throws Exception {
		Product laptop = saveProduct("SKU-1");
		saveStock(saveWarehouse("Lisbon DC", "Lisbon"), laptop, 120);
		saveStock(saveWarehouse("Porto DC", "Porto"), laptop, 30);

		mockMvc.perform(get("/api/stock").param("productId", laptop.getId().toString()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(2)))
			.andExpect(jsonPath("$[*].warehouseName", containsInAnyOrder("Lisbon DC", "Porto DC")))
			.andExpect(jsonPath("$[*].quantity", containsInAnyOrder(120, 30)))
			.andExpect(jsonPath("$[0].productId").value(laptop.getId()));
	}

	@Test
	void returnsEmptyListWhenProductHasNoStock() throws Exception {
		Product laptop = saveProduct("SKU-1");

		mockMvc.perform(get("/api/stock").param("productId", laptop.getId().toString()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(0)));
	}

	@Test
	void returnsNotFoundForUnknownProduct() throws Exception {
		mockMvc.perform(get("/api/stock").param("productId", "0"))
			.andExpect(status().isNotFound());
	}

	@Test
	void returnsBadRequestWhenProductIdIsMissing() throws Exception {
		mockMvc.perform(get("/api/stock").param("productId", ""))
			.andExpect(status().isBadRequest());

	}

	// Children first, so foreign keys don't block the deletes
	private void deleteAll() {
		stockRepository.deleteAll();
		warehouseRepository.deleteAll();
		productRepository.deleteAll();
		manufacturerRepository.deleteAll();
		categoryRepository.deleteAll();
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

	private Warehouse saveWarehouse(String name, String city) {
		Warehouse warehouse = new Warehouse();
		warehouse.setName(name);
		warehouse.setCity(city);
		return warehouseRepository.save(warehouse);
	}

	private WarehouseStock saveStock(Warehouse warehouse, Product product, int quantity) {
		WarehouseStock stock = new WarehouseStock();
		stock.setWarehouse(warehouse);
		stock.setProduct(product);
		stock.setQuantity(quantity);
		return stockRepository.save(stock);
	}

}
