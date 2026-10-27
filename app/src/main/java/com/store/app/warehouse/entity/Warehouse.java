package com.store.app.warehouse.entity;

import com.store.app.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "warehouses")
@Getter
@Setter
public class Warehouse extends BaseEntity {

	@Column(nullable = false, length = 200)
	private String name;

	@Column(nullable = false, length = 100)
	private String city;

}
