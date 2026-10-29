package com.store.app.store.entity;

import com.store.app.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "stores",
		uniqueConstraints = @UniqueConstraint(name = "uk_stores_name_city", columnNames = { "name", "city" }))
@Getter
@Setter
public class Store extends BaseEntity {

	@Column(nullable = false, length = 200)
	private String name;

	@Column(nullable = false, length = 100)
	private String city;

}
