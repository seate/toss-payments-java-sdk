package org.tosspayments.sdk.authorization;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link TossPaymentsAuthentication}.
 */
class TossPaymentsAuthenticationTest {

	/**
	 * Tests null key validation.
	 */
	@Test
	void nullGetEncodedKeyValidation() {
		Assertions.assertThrows(
			IllegalArgumentException.class,
			() -> new TossPaymentsAuthentication(null)
		);
	}

	/**
	 * Tests empty key validation.
	 */
	@Test
	void emptyGetEncodedKeyValidation() {
		Assertions.assertThrows(
			IllegalArgumentException.class,
			() -> new TossPaymentsAuthentication("")
		);
	}

}
