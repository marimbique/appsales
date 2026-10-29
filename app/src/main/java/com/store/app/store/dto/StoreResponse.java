package com.store.app.store.dto;

import java.time.Instant;

public record StoreResponse(

		Long id,

		String name,

		String city,

		Instant createdAt,

		Instant updatedAt) {

}
