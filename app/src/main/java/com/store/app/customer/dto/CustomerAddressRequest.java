package com.store.app.customer.dto;

import com.store.app.customer.entity.AddressType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CustomerAddressRequest(

		@NotNull AddressType addressType,

		@NotBlank @Size(max = 255) String street,

		@Size(max = 50) String doorNumber,

		@NotBlank @Size(max = 20) String postalCode,

		@NotBlank @Size(max = 100) String city,

		@Size(max = 100) String district,

		@NotBlank @Size(max = 100) String country,

		boolean isDefault) {
}
