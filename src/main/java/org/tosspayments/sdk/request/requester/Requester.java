package org.tosspayments.sdk.request.requester;

import java.util.concurrent.CompletableFuture;

/**
 * Interface for making HTTP requests.
 * Provides methods for synchronous and asynchronous GET and POST requests.
 */
public interface Requester {

	/**
	 * Requests to the specified URL using a GET request.
	 *
	 * @param url          the URL to send the GET request to
	 * @param responseType the class of the response type
	 * @param <T>          the type of the response
	 * @return the response object
	 */
	<T> T get(String url, Class<T> responseType);

	/**
	 * Requests to the specified URL using a GET request asynchronously.
	 *
	 * @param url          the URL to send the GET request to
	 * @param responseType the class of the response type
	 * @param <T>          the type of the response
	 * @return a CompletableFuture that will contain the response object
	 */
	<T> CompletableFuture<T> getAsync(String url, Class<T> responseType);

	/**
	 * Requests to the specified URL using a POST request with the given request body.
	 *
	 * @param url          the URL to send the POST request to
	 * @param requestBody  the body of the POST request
	 * @param responseType the class of the response type
	 * @param <T>          the type of the response
	 * @return the response object
	 */
	<T> T post(String url, Object requestBody, Class<T> responseType);

	/**
	 * Requests to the specified URL using a POST request with the given request body asynchronously.
	 *
	 * @param url          the URL to send the POST request to
	 * @param requestBody  the body of the POST request
	 * @param responseType the class of the response type
	 * @param <T>          the type of the response
	 * @return a CompletableFuture that will contain the response object
	 */
	<T> CompletableFuture<T> postAsync(String url, Object requestBody, Class<T> responseType);
}
