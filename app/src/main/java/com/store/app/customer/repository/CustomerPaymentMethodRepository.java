package com.store.app.customer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.store.app.customer.entity.CustomerPaymentMethod;

public interface CustomerPaymentMethodRepository extends JpaRepository<CustomerPaymentMethod, Long> {

	List<CustomerPaymentMethod> findAllByCustomer_IdOrderById(Long customerId);

	Optional<CustomerPaymentMethod> findByIdAndCustomer_Id(Long id, Long customerId);
}
