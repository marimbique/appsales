package com.store.app.customer.dto;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CustomerRequestDto {

	@NotBlank
	@Size(max = 30)
	private String customerCode;

	@NotBlank
	@Size(max = 100)
	private String firstName;

	@NotBlank
	@Size(max = 100)
	private String lastName;

	@NotBlank
	@Email
	@Size(max = 255)
	private String email;

	@Size(max = 30)
	private String phone;

	@Size(max = 50)
	private String taxNumber;

	private LocalDate birthDate;

	private List<@Valid CustomerPaymentMethodRequest> cpmr;

	private List<@Valid CustomerAddressRequest> car;

}
