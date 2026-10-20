package com.store.app.customer.entity;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "customer_addresses")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CustomerAddress {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "customer_id", nullable = false)
	private Customer customer;

	@Enumerated(EnumType.STRING)
	@JdbcTypeCode(SqlTypes.NAMED_ENUM)
	@Column(name = "address_type", nullable = false)
	private AddressType addressType;

	@Column(nullable = false, length = 255)
	private String street;

	@Column(name = "door_number", length = 50)
	private String doorNumber;

	@Column(name = "postal_code", nullable = false, length = 20)
	private String postalCode;

	@Column(nullable = false, length = 100)
	private String city;

	@Column(length = 100)
	private String district;

	@Column(nullable = false, length = 100)
	private String country;

	@Column(name = "is_default", nullable = false)
	private boolean isDefault;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	public CustomerAddress(Customer customer, AddressType addressType, String street, String doorNumber,
			String postalCode, String city, String district, String country, boolean isDefault) {
		this.customer = customer;
		update(addressType, street, doorNumber, postalCode, city, district, country, isDefault);
	}

	public void update(AddressType addressType, String street, String doorNumber, String postalCode,
			String city, String district, String country, boolean isDefault) {
		this.addressType = addressType;
		this.street = street;
		this.doorNumber = doorNumber;
		this.postalCode = postalCode;
		this.city = city;
		this.district = district;
		this.country = country;
		this.isDefault = isDefault;
	}
}
