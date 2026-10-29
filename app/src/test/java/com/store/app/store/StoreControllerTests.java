package com.store.app.store;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.store.app.store.repository.StoreRepository;

@SpringBootTest
@AutoConfigureMockMvc
class StoreControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private StoreRepository storeRepository;

	// @SpringBootTest does not roll back, so start each test from an empty table
	@BeforeEach
	void setUp() {
		storeRepository.deleteAll();
	}

	@Test
	void createsStoreAndReturnsLocation() throws Exception {
		createStore("Lisbon Store", "Lisbon")
			.andExpect(status().isCreated())
			.andExpect(header().string("Location", matchesPattern(".*/api/stores/\\d+")))
			.andExpect(jsonPath("$.id").isNumber())
			.andExpect(jsonPath("$.name").value("Lisbon Store"))
			.andExpect(jsonPath("$.city").value("Lisbon"))
			.andExpect(jsonPath("$.createdAt").exists())
			.andExpect(jsonPath("$.updatedAt").exists());

		assertThat(storeRepository.count()).isEqualTo(1);
	}

	@Test
	void readsCreatedStoreById() throws Exception {
		String location = createStore("Lisbon Store", "Lisbon")
			.andExpect(status().isCreated())
			.andReturn()
			.getResponse()
			.getHeader("Location");

		// Follow the Location header so the test also proves it points at the new store
		mockMvc.perform(get(location))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").isNumber())
			.andExpect(jsonPath("$.name").value("Lisbon Store"))
			.andExpect(jsonPath("$.city").value("Lisbon"));
	}

	@Test
	void returnsBadRequestForBlankName() throws Exception {
		createStore("", "Lisbon").andExpect(status().isBadRequest());
		createStore("   ", "Lisbon").andExpect(status().isBadRequest());

		assertThat(storeRepository.count()).isZero();
	}

	@Test
	void returnsConflictForDuplicateNameInSameCity() throws Exception {
		createStore("Lisbon Store", "Lisbon").andExpect(status().isCreated());

		createStore("Lisbon Store", "Lisbon").andExpect(status().isConflict());

		assertThat(storeRepository.count()).isEqualTo(1);
	}

	@Test
	void returnsNotFoundForUnknownStore() throws Exception {
		mockMvc.perform(get("/api/stores/{id}", 999_999L)).andExpect(status().isNotFound());
	}

	private ResultActions createStore(String name, String city) throws Exception {
		return mockMvc.perform(post("/api/stores").contentType(MediaType.APPLICATION_JSON).content(json(name, city)));
	}

	private static String json(String name, String city) {
		return """
				{"name": "%s", "city": "%s"}
				""".formatted(name, city);
	}

}
