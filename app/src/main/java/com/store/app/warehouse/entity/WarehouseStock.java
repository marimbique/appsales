package com.store.app.warehouse.entity;

import org.hibernate.annotations.Check;

import com.store.app.common.entity.BaseEntity;
import com.store.app.product.entity.Product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "warehouse_stock",
		uniqueConstraints = @UniqueConstraint(name = "uk_warehouse_stock_warehouse_product",
				columnNames = { "warehouse_id", "product_id" }))
@Getter
@Setter
public class WarehouseStock extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "warehouse_id", nullable = false)
	private Warehouse warehouse;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	@Check(constraints = "quantity >= 0")
	@Column(nullable = false)
	private int quantity;

	public void setQuantity(int quantity) {
	if (quantity < 0) {
		throw new IllegalArgumentException("Quantity cannot be negative");
	}
	this.quantity = quantity;
}


}
