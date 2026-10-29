package com.store.app.store.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import com.store.app.store.entity.Store;

@DataJpaTest
class StoreRepositoryTests {

	@Autowired
	private StoreRepository repository;

	@Autowired
	private TestEntityManager entityManager;

	private Store storeOne;
	private Store storeTwo;

	@BeforeEach
	void setUp() {
		storeOne = new Store();
		storeOne.setName("Lisbon Store");
		storeOne.setCity("Lisbon");

		storeTwo = new Store();
		storeTwo.setName("Porto Store");
		storeTwo.setCity("Porto");

		repository.save(storeOne);
		repository.save(storeTwo);
	}

	@Test
	void savesAndFindsStoreById() {
		Store store = new Store();
		store.setName("London Store");
		store.setCity("London");

		Long id = repository.saveAndFlush(store).getId();
		// Detach everything so findById issues a real SELECT instead of returning the cached instance
		entityManager.clear();

		Optional<Store> found = repository.findById(id);

		assertThat(found).hasValueSatisfying(loaded -> {
			assertThat(loaded).isNotSameAs(store);
			assertThat(loaded.getId()).isEqualTo(id);
			assertThat(loaded.getName()).isEqualTo("London Store");
			assertThat(loaded.getCity()).isEqualTo("London");
			assertThat(loaded.getCreatedAt()).isNotNull();
			assertThat(loaded.getUpdatedAt()).isNotNull();
		});
	}

	@Test
	void existsByNameAndCityMatchesOnlySameNameAndCity() {
		// setUp stores "Lisbon Store"/Lisbon and "Porto Store"/Porto
		assertThat(repository.existsByNameAndCity("Lisbon Store", "Lisbon")).isTrue();
		assertThat(repository.existsByNameAndCity("Porto Store", "Porto")).isTrue();

		// Swapping cities pairs a real name with a real city, but not as a stored combination
		assertThat(repository.existsByNameAndCity("Lisbon Store", "Porto")).isFalse();
		assertThat(repository.existsByNameAndCity("Porto Store", "Lisbon")).isFalse();
		assertThat(repository.existsByNameAndCity("Faro Store", "Lisbon")).isFalse();
	}

	@Test
	void allowsSameNameInDifferentCities() {
		Store london = new Store();
		london.setName("Apple Store");
		london.setCity("London");

		Store paris = new Store();
		paris.setName("Apple Store");
		paris.setCity("Paris");

		// saveAndFlush runs the INSERT now, so a constraint on name alone would fail here
		Long londonId = repository.saveAndFlush(london).getId();
		Long parisId = repository.saveAndFlush(paris).getId();

		assertThat(londonId).isNotNull();
		assertThat(parisId).isNotNull().isNotEqualTo(londonId);
		assertThat(repository.existsByNameAndCity("Apple Store", "London")).isTrue();
		assertThat(repository.existsByNameAndCity("Apple Store", "Paris")).isTrue();
	}

	@Test
	void rejectsDuplicateNameAndCity() {
		// Same name and city as storeOne from setUp
		Store duplicate = new Store();
		duplicate.setName("Lisbon Store");
		duplicate.setCity("Lisbon");

		assertThatThrownBy(() -> repository.saveAndFlush(duplicate))
			.isInstanceOf(DataIntegrityViolationException.class)
			// H2 reports constraint names in upper case, Postgres in lower case
			.message()
			.containsIgnoringCase("uk_stores_name_city");
	}

}
