package com.store.app.customer.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.store.app.customer.dto.CustomerRequestDto;
import com.store.app.customer.dto.CustomerAddressResponse;
import com.store.app.customer.dto.CustomerDetailsResponseDto;
import com.store.app.customer.dto.CustomerPaymentMethodResponse;
import com.store.app.customer.dto.CustomerResponseDto;
import com.store.app.customer.entity.Customer;
import com.store.app.customer.entity.CustomerAddress;
import com.store.app.customer.entity.CustomerPaymentMethod;
import com.store.app.customer.mapper.CustomerMapper;
import com.store.app.customer.repository.CustomerAddressRepository;
import com.store.app.customer.repository.CustomerPaymentMethodRepository;
import com.store.app.customer.repository.CustomerRepository;

@Service
@Transactional
public class CustomerService {

	private final CustomerRepository customerRepository;
	private final CustomerAddressRepository addressRepository;
	private final CustomerPaymentMethodRepository paymentMethodRepository;
	private final CustomerMapper customerMapper;

	public CustomerService(CustomerRepository customerRepository, CustomerAddressRepository addressRepository,
			CustomerPaymentMethodRepository paymentMethodRepository, CustomerMapper customerMapper) {
		this.customerRepository = customerRepository;
		this.addressRepository = addressRepository;
		this.paymentMethodRepository = paymentMethodRepository;
		this.customerMapper = customerMapper;
	}

	public CustomerResponseDto create(CustomerRequestDto request) {
		if (customerRepository.existsByCustomerCode(request.getCustomerCode())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Customer code already exists");
		}
		if (customerRepository.existsByEmail(request.getEmail())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
		}

		Customer customer = customerMapper.toEntity(request);
		if (request.getCar() != null) {
			request.getCar().forEach(address -> customer.addAddress(new CustomerAddress(customer,
					address.addressType(), address.street(), address.doorNumber(), address.postalCode(),
					address.city(), address.district(), address.country(), address.isDefault())));
		}
		if (request.getCpmr() != null) {
			request.getCpmr().forEach(method -> customer.addPaymentMethod(new CustomerPaymentMethod(customer,
					method.paymentType(), method.cardHolder(), method.cardLastDigits(), method.expiryMonth(),
					method.expiryYear(), method.iban(), method.isDefault(),
					method.active() == null || method.active())));
		}
		Customer savedCustomer = customerRepository.save(customer);
		
		return customerMapper.toResponse(savedCustomer);
	}

	@Transactional(readOnly = true)
	public List<CustomerResponseDto> findAll() {
		return customerRepository.findAll().stream()
				.map(customerMapper::toResponse)
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public CustomerDetailsResponseDto findById(Long id) {
		Customer customer = customerRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
		CustomerResponseDto customerResponse = customerMapper.toResponse(customer);
		List<CustomerAddressResponse> addresses = addressRepository.findAllByCustomer_IdOrderById(id).stream()
				.map(CustomerAddressResponse::from)
				.toList();
		List<CustomerPaymentMethodResponse> paymentMethods = paymentMethodRepository
				.findAllByCustomer_IdOrderById(id).stream()
				.map(CustomerPaymentMethodResponse::from)
				.toList();

		return new CustomerDetailsResponseDto(
				customerResponse.getId(),
				customerResponse.getCustomerCode(),
				customerResponse.getFirstName(),
				customerResponse.getLastName(),
				customerResponse.getEmail(),
				customerResponse.getPhone(),
				customerResponse.getTaxNumber(),
				customerResponse.getBirthDate(),
				customerResponse.isActive(),
				customerResponse.getCreatedAt(),
				customerResponse.getUpdatedAt(),
				addresses,
				paymentMethods);
	}
}
