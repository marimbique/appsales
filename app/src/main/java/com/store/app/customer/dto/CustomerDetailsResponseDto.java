package com.store.app.customer.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record CustomerDetailsResponseDto(
		Long id,
		String customerCode,
		String firstName,
		String lastName,
		String email,
		String phone,
		String taxNumber,
		LocalDate birthDate,
		boolean active,
		Instant createdAt,
		Instant updatedAt,
		List<CustomerAddressResponse> addresses,
		List<CustomerPaymentMethodResponse> paymentMethods) {
}
