package com.store.app.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class TestAssertions {

	private TestAssertions() {
	}

	public static void assertStatus(ThrowingCallable call, HttpStatus expected) {
		assertThatThrownBy(call).isInstanceOfSatisfying(ResponseStatusException.class,
				ex -> assertThat(ex.getStatusCode()).isEqualTo(expected));
	}

}
