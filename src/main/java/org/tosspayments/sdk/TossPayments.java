package org.tosspayments.sdk;

import org.tosspayments.sdk.request.requester.Requester;
import org.tosspayments.sdk.request.requester.RequesterBuilder;

/**
 * Root Module for Toss Payments SDK.
 */
public class TossPayments {

	/**
	 * The endpoint URL for Toss Payments API.
	 * This is the base URL for all API requests.
	 */
	public static final String ENDPOINT = "https://api.tosspayments.com/v1/inform";


	private final Requester requester;

	/**
	 * Creates a new instance of TossPayments with the provided secret key.
	 * @param secretKey The secret key for authentication with Toss Payments.
	 */
	public TossPayments(String secretKey) {
		this.requester = new RequesterBuilder(ENDPOINT, secretKey)
			.build();
	}

	/**
	 * Creates a new instance of TossPayments with the provided secret key and custom requester.
	 * @param secretKey The secret key for authentication with Toss Payments.
	 * @param requester Custom requester for handling requests.
	 */
	public TossPayments(String secretKey, Requester requester) {
		this.requester = new RequesterBuilder(ENDPOINT, secretKey)
			.withRequester(requester)
			.build();
	}

}
