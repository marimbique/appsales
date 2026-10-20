package com.store.app.customer.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import com.store.app.customer.dto.CustomerRequestDto;
import com.store.app.customer.dto.CustomerResponseDto;
import com.store.app.customer.entity.Customer;

@Mapper(
		componentModel = MappingConstants.ComponentModel.SPRING,
		unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CustomerMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "addresses", ignore = true)
	@Mapping(target = "paymentMethods", ignore = true)
	Customer toEntity(CustomerRequestDto request);

	CustomerResponseDto toResponse(Customer customer);
}
