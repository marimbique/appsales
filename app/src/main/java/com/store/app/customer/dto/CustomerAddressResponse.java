package com.store.app.customer.dto;

import java.time.Instant;

import com.store.app.customer.entity.AddressType;
import com.store.app.customer.entity.CustomerAddress;

public record CustomerAddressResponse(
		Long id,
		Long customerId,
		AddressType addressType,
		String street,
		String doorNumber,
		String postalCode,
		String city,
		String district,
		String country,
		boolean isDefault,
		Instant createdAt,
		Instant updatedAt) {

	public static CustomerAddressResponse from(CustomerAddress address) {
		return new CustomerAddressResponse(
				address.getId(),
				address.getCustomer().getId(),
				address.getAddressType(),
				address.getStreet(),
				address.getDoorNumber(),
				address.getPostalCode(),
				address.getCity(),
				address.getDistrict(),
				address.getCountry(),
				address.isDefault(),
				address.getCreatedAt(),
				address.getUpdatedAt());
	}
}
