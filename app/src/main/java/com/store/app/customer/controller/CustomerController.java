package com.store.app.customer.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.store.app.customer.dto.CustomerAddressRequest;
import com.store.app.customer.dto.CustomerAddressResponse;
import com.store.app.customer.dto.CustomerPaymentMethodRequest;
import com.store.app.customer.dto.CustomerPaymentMethodResponse;
import com.store.app.customer.dto.CustomerRequestDto;
import com.store.app.customer.dto.CustomerResponseDto;
import com.store.app.customer.service.CustomerAddressService;
import com.store.app.customer.service.CustomerPaymentMethodService;
import com.store.app.customer.service.CustomerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

	private final CustomerService customerService;
	private final CustomerAddressService customerAddressService;
	private final CustomerPaymentMethodService customerPaymentMethodService;

	public CustomerController(CustomerService customerService, CustomerAddressService customerAddressService,
			CustomerPaymentMethodService customerPaymentMethodService) {
		this.customerService = customerService;
		this.customerAddressService = customerAddressService;
		this.customerPaymentMethodService = customerPaymentMethodService;
	}

	@PostMapping
	public ResponseEntity<CustomerResponseDto> create(@Valid @RequestBody CustomerRequestDto request) {
		CustomerResponseDto customer = customerService.create(request);
		return ResponseEntity.created(URI.create("/api/customers/" + customer.getId())).body(customer);
	}

	@GetMapping
	public List<CustomerResponseDto> findAll() {
		return customerService.findAll();
	}

	@GetMapping("/{id}")
	public CustomerResponseDto findById(@PathVariable Long id) {
		return customerService.findById(id);
	}

	@PostMapping("/{customerId}/addresses")
	public ResponseEntity<CustomerAddressResponse> createAddress(@PathVariable Long customerId,
			@Valid @RequestBody CustomerAddressRequest request) {
		CustomerAddressResponse address = customerAddressService.create(customerId, request);
		return ResponseEntity.created(URI.create("/api/customers/" + customerId + "/addresses/" + address.id()))
				.body(address);
	}

	@PutMapping("/{customerId}/addresses/{id}")
	public CustomerAddressResponse updateAddress(@PathVariable Long customerId, @PathVariable Long id,
			@Valid @RequestBody CustomerAddressRequest request) {
		return customerAddressService.update(customerId, id, request);
	}

	@GetMapping("/{customerId}/addresses")
	public List<CustomerAddressResponse> findAddresses(@PathVariable Long customerId) {
		return customerAddressService.findAll(customerId);
	}

	@GetMapping("/{customerId}/addresses/{id}")
	public CustomerAddressResponse findAddressById(@PathVariable Long customerId, @PathVariable Long id) {
		return customerAddressService.findById(customerId, id);
	}

	@PostMapping("/{customerId}/payment-methods")
	public ResponseEntity<CustomerPaymentMethodResponse> createPaymentMethod(@PathVariable Long customerId,
			@Valid @RequestBody CustomerPaymentMethodRequest request) {
		CustomerPaymentMethodResponse method = customerPaymentMethodService.create(customerId, request);
		return ResponseEntity.created(URI.create(
				"/api/customers/" + customerId + "/payment-methods/" + method.id())).body(method);
	}

	@PutMapping("/{customerId}/payment-methods/{id}")
	public CustomerPaymentMethodResponse updatePaymentMethod(@PathVariable Long customerId, @PathVariable Long id,
			@Valid @RequestBody CustomerPaymentMethodRequest request) {
		return customerPaymentMethodService.update(customerId, id, request);
	}

	@GetMapping("/{customerId}/payment-methods")
	public List<CustomerPaymentMethodResponse> findPaymentMethods(@PathVariable Long customerId) {
		return customerPaymentMethodService.findAll(customerId);
	}

	@GetMapping("/{customerId}/payment-methods/{id}")
	public CustomerPaymentMethodResponse findPaymentMethodById(@PathVariable Long customerId,
			@PathVariable Long id) {
		return customerPaymentMethodService.findById(customerId, id);
	}
}
