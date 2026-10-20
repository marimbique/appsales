package com.store.app.customer.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.store.app.customer.dto.CustomerAddressRequest;
import com.store.app.customer.dto.CustomerAddressResponse;
import com.store.app.customer.entity.Customer;
import com.store.app.customer.entity.CustomerAddress;
import com.store.app.customer.repository.CustomerAddressRepository;
import com.store.app.customer.repository.CustomerRepository;

@Service
@Transactional
public class CustomerAddressService {

	private final CustomerRepository customerRepository;
	private final CustomerAddressRepository addressRepository;

	public CustomerAddressService(CustomerRepository customerRepository,
			CustomerAddressRepository addressRepository) {
		this.customerRepository = customerRepository;
		this.addressRepository = addressRepository;
	}

	public CustomerAddressResponse create(Long customerId, CustomerAddressRequest request) {
		Customer customer = findCustomer(customerId);
		CustomerAddress address = new CustomerAddress(customer, request.addressType(), request.street(),
				request.doorNumber(), request.postalCode(), request.city(), request.district(),
				request.country(), request.isDefault());
		return CustomerAddressResponse.from(addressRepository.save(address));
	}

	public CustomerAddressResponse update(Long customerId, Long id, CustomerAddressRequest request) {
		CustomerAddress address = findAddress(customerId, id);
		address.update(request.addressType(), request.street(), request.doorNumber(), request.postalCode(),
				request.city(), request.district(), request.country(), request.isDefault());
		return CustomerAddressResponse.from(addressRepository.save(address));
	}

	@Transactional(readOnly = true)
	public List<CustomerAddressResponse> findAll(Long customerId) {
		findCustomer(customerId);
		return addressRepository.findAllByCustomer_IdOrderById(customerId).stream()
				.map(CustomerAddressResponse::from)
				.toList();
	}

	@Transactional(readOnly = true)
	public CustomerAddressResponse findById(Long customerId, Long id) {
		return CustomerAddressResponse.from(findAddress(customerId, id));
	}

	private Customer findCustomer(Long customerId) {
		return customerRepository.findById(customerId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
	}

	private CustomerAddress findAddress(Long customerId, Long id) {
		findCustomer(customerId);
		return addressRepository.findByIdAndCustomer_Id(id, customerId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer address not found"));
	}
}
