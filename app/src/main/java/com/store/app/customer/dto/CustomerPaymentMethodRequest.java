package com.store.app.customer.dto;

import com.store.app.customer.entity.PaymentType;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CustomerPaymentMethodRequest(

		@NotNull PaymentType paymentType,

		@Size(max = 150) String cardHolder,

		@Size(min = 4, max = 4) String cardLastDigits,

		@Min(1) @Max(12) Integer expiryMonth,

		@Min(2000) @Max(9999) Integer expiryYear,

		@Size(max = 50) String iban,

		boolean isDefault,

		Boolean active) {
}
