package com.store.app.store.service;

import static com.store.app.common.TestAssertions.assertStatus;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;

import com.store.app.store.dto.StoreRequest;
import com.store.app.store.dto.StoreResponse;
import com.store.app.store.entity.Store;
import com.store.app.store.mapper.StoreMapperImpl;
import com.store.app.store.repository.StoreRepository;

@ExtendWith(MockitoExtension.class)
class StoreServiceTests {

	private static final StoreRequest REQUEST = new StoreRequest("Good books", "Lisbon");

	@Mock
	private StoreRepository storeRepository;

	private StoreService service;

	@BeforeEach
	void setUp() {
		service = new StoreService(storeRepository, new StoreMapperImpl());
	}

	@Test
	void createSavesStoreAndReturnsResponse() {
		when(storeRepository.saveAndFlush(any(Store.class))).thenReturn(store(1L, "Good books", "Lisbon"));

		StoreResponse response = service.create(REQUEST);

		ArgumentCaptor<Store> saved = ArgumentCaptor.forClass(Store.class);
		verify(storeRepository).saveAndFlush(saved.capture());
		assertThat(saved.getValue().getId()).isNull();
		assertThat(saved.getValue().getName()).isEqualTo("Good books");
		assertThat(saved.getValue().getCity()).isEqualTo("Lisbon");

		assertThat(response.id()).isEqualTo(1L);
		assertThat(response.name()).isEqualTo("Good books");
		assertThat(response.city()).isEqualTo("Lisbon");
	}

	@Test
	void createRejectsDuplicateNameInSameCity() {
		when(storeRepository.existsByNameAndCity("Good books", "Lisbon")).thenReturn(true);

		assertStatus(() -> service.create(REQUEST), HttpStatus.CONFLICT);
		verify(storeRepository, never()).saveAndFlush(any());
	}

	@Test
	void createMapsConcurrentDuplicateToConflict() {
		// Another request inserted the same store between the exists check and the insert
		when(storeRepository.saveAndFlush(any(Store.class)))
			.thenThrow(new DataIntegrityViolationException("uk_stores_name_city"));

		assertStatus(() -> service.create(REQUEST), HttpStatus.CONFLICT);
	}

	@Test
	void findByIdReturnsStore() {
		when(storeRepository.findById(1L)).thenReturn(Optional.of(store(1L, "Good books", "Lisbon")));

		StoreResponse response = service.findById(1L);

		assertThat(response.id()).isEqualTo(1L);
		assertThat(response.name()).isEqualTo("Good books");
		assertThat(response.city()).isEqualTo("Lisbon");
	}

	@Test
	void findByIdThrowsNotFoundForUnknownStore() {
		when(storeRepository.findById(1L)).thenReturn(Optional.empty());

		assertStatus(() -> service.findById(1L), HttpStatus.NOT_FOUND);
	}

	private static Store store(Long id, String name, String city) {
		Store store = new Store();
		store.setId(id);
		store.setName(name);
		store.setCity(city);
		return store;
	}

}
