package org.tosspayments.sdk.exception;

import lombok.Getter;

/**
 * Exception thrown when a request to the Toss API fails.
 * Contains the HTTP status code and response body for debugging purposes.
 */
@Getter
public class TossApiException extends RuntimeException {

	/**
	 * Status code when the request to the Toss API fails.
	 */
	private final int statusCode;

	/**
	 * Constructor for TossApiException.
	 * @param statusCode the HTTP status code of the failed request
	 */
	public TossApiException(int statusCode) {
		super("Toss Api http request failed " + statusCode);
		this.statusCode = statusCode;
	}

}
