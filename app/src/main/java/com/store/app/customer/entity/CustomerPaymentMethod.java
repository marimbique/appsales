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
@Table(name = "customer_payment_methods")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CustomerPaymentMethod {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "customer_id", nullable = false)
	private Customer customer;

	@Enumerated(EnumType.STRING)
	@JdbcTypeCode(SqlTypes.NAMED_ENUM)
	@Column(name = "payment_type", nullable = false)
	private PaymentType paymentType;

	@Column(name = "card_holder", length = 150)
	private String cardHolder;

	@Column(name = "card_last_digits", length = 4)
	private String cardLastDigits;

	@Column(name = "expiry_month")
	private Integer expiryMonth;

	@Column(name = "expiry_year")
	private Integer expiryYear;

	@Column(length = 50)
	private String iban;

	@Column(name = "is_default", nullable = false)
	private boolean isDefault;

	@Column(nullable = false)
	private boolean active = true;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	public CustomerPaymentMethod(Customer customer, PaymentType paymentType, String cardHolder,
			String cardLastDigits, Integer expiryMonth, Integer expiryYear, String iban,
			boolean isDefault, boolean active) {
		this.customer = customer;
		update(paymentType, cardHolder, cardLastDigits, expiryMonth, expiryYear, iban, isDefault, active);
	}

	public void update(PaymentType paymentType, String cardHolder, String cardLastDigits,
			Integer expiryMonth, Integer expiryYear, String iban, boolean isDefault, boolean active) {
		this.paymentType = paymentType;
		this.cardHolder = cardHolder;
		this.cardLastDigits = cardLastDigits;
		this.expiryMonth = expiryMonth;
		this.expiryYear = expiryYear;
		this.iban = iban;
		this.isDefault = isDefault;
		this.active = active;
	}
}
