package com.store.app.customer.dto;

import java.time.Instant;

import com.store.app.customer.entity.CustomerPaymentMethod;
import com.store.app.customer.entity.PaymentType;

public record CustomerPaymentMethodResponse(
		Long id,
		Long customerId,
		PaymentType paymentType,
		String cardHolder,
		String cardLastDigits,
		Integer expiryMonth,
		Integer expiryYear,
		String iban,
		boolean isDefault,
		boolean active,
		Instant createdAt,
		Instant updatedAt) {

	public static CustomerPaymentMethodResponse from(CustomerPaymentMethod method) {
		return new CustomerPaymentMethodResponse(
				method.getId(),
				method.getCustomer().getId(),
				method.getPaymentType(),
				method.getCardHolder(),
				method.getCardLastDigits(),
				method.getExpiryMonth(),
				method.getExpiryYear(),
				method.getIban(),
				method.isDefault(),
				method.isActive(),
				method.getCreatedAt(),
				method.getUpdatedAt());
	}
}
