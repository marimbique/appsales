package com.store.app.store.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import com.store.app.store.dto.StoreRequest;
import com.store.app.store.dto.StoreResponse;
import com.store.app.store.entity.Store;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface StoreMapper {

	StoreResponse toResponse(Store store);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	Store toEntity(StoreRequest request);

}
