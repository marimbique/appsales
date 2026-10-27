package com.store.app.customer.entity;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Customer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "customer_code", nullable = false, unique = true, length = 30)
	private String customerCode;

	@Column(name = "first_name", nullable = false, length = 100)
	private String firstName;

	@Column(name = "last_name", nullable = false, length = 100)
	private String lastName;

	@Column(nullable = false, unique = true, length = 255)
	private String email;

	@Column(length = 30)
	private String phone;

	@Column(name = "tax_number", length = 50)
	private String taxNumber;

	@Column(name = "birth_date")
	private LocalDate birthDate;

	@Column(nullable = false)
	private final boolean active = true;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CustomerAddress> addresses = new ArrayList<>();

	@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CustomerPaymentMethod> paymentMethods = new ArrayList<>();

	public Customer(String customerCode, String firstName, String lastName, String email,
			String phone, String taxNumber, LocalDate birthDate) {
		this.customerCode = customerCode;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.phone = phone;
		this.taxNumber = taxNumber;
		this.birthDate = birthDate;
	}

	public void addAddress(CustomerAddress address) {
		addresses.add(address);
	}

	public void addPaymentMethod(CustomerPaymentMethod paymentMethod) {
		paymentMethods.add(paymentMethod);
	}
}
