package org.tosspayments.sdk.request.requester;

import org.tosspayments.sdk.authorization.TossPaymentsAuthentication;
import org.tosspayments.sdk.request.requester.httpclient.HttpClientRequester;

/**
 * Builder for creating a {@link Requester} instance.
 * This class allows you to specify a custom requester or use the default HTTP client requester.
 */
public class RequesterBuilder {

	private final String endpoint;

	private final TossPaymentsAuthentication authorization;

	private Requester requester = null;

	/**
	 * Creates a new RequesterBuilder with the specified endpoint and secret key.
	 * @param endpoint the API endpoint to use for requests
	 * @param secretKey the secret key for authentication
	 */
	public RequesterBuilder(String endpoint, String secretKey) {
		this.endpoint = endpoint;
		this.authorization = new TossPaymentsAuthentication(secretKey);
	}

	/**
	 * Sets a custom requester to be used.
	 * @param requester the custom requester to use.
	 * @return the RequesterBuilder instance for method chaining.
	 */
	public RequesterBuilder withRequester(Requester requester) {
		this.requester = requester;

		return this;
	}

	/**
	 * Builds the Requester instance.
	 * If no custom requester is set, it will return a default HTTP client requester.
	 * @return the Requester instance
	 */
	public Requester build() {
		if (requester == null) {
			requester = getDefaultRequester();
		}

		return requester;
	}

	/**
	 * Returns Default HTTP client requester.
	 * @return the default HTTP client requester
	 */
	private Requester getDefaultRequester() {
		return new HttpClientRequester(endpoint, authorization);
	}

}
