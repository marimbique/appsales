package com.store.app.customer.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.store.app.customer.dto.CustomerPaymentMethodRequest;
import com.store.app.customer.dto.CustomerPaymentMethodResponse;
import com.store.app.customer.entity.Customer;
import com.store.app.customer.entity.CustomerPaymentMethod;
import com.store.app.customer.repository.CustomerPaymentMethodRepository;
import com.store.app.customer.repository.CustomerRepository;

@Service
@Transactional
public class CustomerPaymentMethodService {

	private final CustomerRepository customerRepository;
	private final CustomerPaymentMethodRepository paymentMethodRepository;

	public CustomerPaymentMethodService(CustomerRepository customerRepository,
			CustomerPaymentMethodRepository paymentMethodRepository) {
		this.customerRepository = customerRepository;
		this.paymentMethodRepository = paymentMethodRepository;
	}

	public CustomerPaymentMethodResponse create(Long customerId, CustomerPaymentMethodRequest request) {
		Customer customer = findCustomer(customerId);
		CustomerPaymentMethod method = new CustomerPaymentMethod(customer, request.paymentType(),
				request.cardHolder(), request.cardLastDigits(), request.expiryMonth(), request.expiryYear(),
				request.iban(), request.isDefault(), request.active() == null || request.active());
		return CustomerPaymentMethodResponse.from(paymentMethodRepository.save(method));
	}

	public CustomerPaymentMethodResponse update(Long customerId, Long id, CustomerPaymentMethodRequest request) {
		CustomerPaymentMethod method = findPaymentMethod(customerId, id);
		method.update(request.paymentType(), request.cardHolder(), request.cardLastDigits(),
				request.expiryMonth(), request.expiryYear(), request.iban(), request.isDefault(),
				request.active() == null || request.active());
		return CustomerPaymentMethodResponse.from(paymentMethodRepository.save(method));
	}

	@Transactional(readOnly = true)
	public List<CustomerPaymentMethodResponse> findAll(Long customerId) {
		findCustomer(customerId);
		return paymentMethodRepository.findAllByCustomer_IdOrderById(customerId).stream()
				.map(CustomerPaymentMethodResponse::from)
				.toList();
	}

	@Transactional(readOnly = true)
	public CustomerPaymentMethodResponse findById(Long customerId, Long id) {
		return CustomerPaymentMethodResponse.from(findPaymentMethod(customerId, id));
	}

	private Customer findCustomer(Long customerId) {
		return customerRepository.findById(customerId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
	}

	private CustomerPaymentMethod findPaymentMethod(Long customerId, Long id) {
		findCustomer(customerId);
		return paymentMethodRepository.findByIdAndCustomer_Id(id, customerId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer payment method not found"));
	}
}
