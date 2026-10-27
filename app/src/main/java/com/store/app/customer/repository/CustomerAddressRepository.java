package com.store.app.customer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.store.app.customer.entity.CustomerAddress;

public interface CustomerAddressRepository extends JpaRepository<CustomerAddress, Long> {

	List<CustomerAddress> findAllByCustomer_IdOrderById(Long customerId);

	Optional<CustomerAddress> findByIdAndCustomer_Id(Long id, Long customerId);
}
