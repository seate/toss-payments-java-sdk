package org.tosspayments.sdk.authorization;

import lombok.Getter;

/**
 * Represents the authentication details for Toss Payments.
 * This class encapsulates the secret key used for authentication.
 */
public class TossPaymentsAuthentication {

	@Getter
	private final String secretKey;

	/**
	 * Creates an instance of TossPaymentsAuthentication with the provided secret key.
	 * @param secretKey the secret key for Toss API
	 */
	public TossPaymentsAuthentication(String secretKey) {
		if (secretKey == null || secretKey.isEmpty()) {
			throw new IllegalArgumentException("Secret key must not be null or empty");
		}

		this.secretKey = secretKey;
	}

}
