package com.store.app.store.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.store.app.store.dto.StoreRequest;
import com.store.app.store.dto.StoreResponse;
import com.store.app.store.mapper.StoreMapper;
import com.store.app.store.repository.StoreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreService {

	private final StoreRepository storeRepository;

	private final StoreMapper storeMapper;

	public StoreResponse findById(Long id) {
		return storeRepository.findById(id)
			.map(storeMapper::toResponse)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Store " + id + " not found"));
	}

	@Transactional
	public StoreResponse create(StoreRequest request) {
		if (storeRepository.existsByNameAndCity(request.name(), request.city())) {
			throw storeAlreadyExists(request.name(), request.city());
		}
		try {
			// Flush so a concurrent insert that slipped past the check hits uk_stores_name_city here, not at commit
			return storeMapper.toResponse(storeRepository.saveAndFlush(storeMapper.toEntity(request)));
		}
		catch (DataIntegrityViolationException ex) {
			throw storeAlreadyExists(request.name(), request.city());
		}
	}

	private static ResponseStatusException storeAlreadyExists(String name, String city) {
		return new ResponseStatusException(HttpStatus.CONFLICT,
				"Store " + name + " already exists in " + city);
	}

}
