package org.tosspayments.sdk.request.requester;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Offer a base implementation for requesters that need to handle HTTP requests.
 */
public abstract class AbstractRequester implements Requester {

	/**
	 * Default connection timeout in seconds.
	 */
	protected static final Integer DEFAULT_CONNECT_TIMEOUT = 10;

	private static final String TOKEN_PREFIX = "Basic ";

	private static final Map<String, String> DEFAULT_HEADERS = Map.of(
		"Content-Type", "application/json",
		"Accept", "application/json",
		"Authorization", TOKEN_PREFIX,
		"Idempotency-Key", ""
	);


	/**
	 * Get the default headers for requests, including an Authorization header with the provided secret key.
	 *
	 * @param secretKey The secret key to be used for authorization.
	 * @return A map of default headers including the Authorization header.
	 */
	protected Map<String, String> getDefaultHeaders(String secretKey) {
		return new HashMap<>(DEFAULT_HEADERS) {
			{
				put("Authorization", TOKEN_PREFIX + Base64.getEncoder().encodeToString((secretKey + ":").getBytes()));
				put("Idempotency-Key", UUID.randomUUID().toString());
			}
		};
	}

}
