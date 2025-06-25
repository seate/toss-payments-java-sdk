package org.tosspayments.sdk.request.requester.httpclient;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.tosspayments.sdk.authorization.TossPaymentsAuthentication;
import org.tosspayments.sdk.exception.TossApiException;
import org.tosspayments.sdk.request.requester.AbstractRequester;
import org.tosspayments.sdk.request.requester.Requester;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Implementation of {@link Requester} using Java's built-in HttpClient.
 */
public class HttpClientRequester extends AbstractRequester {

	private final String endpoint;

	private final Map<String, String> defaultHeaders;

	private final HttpClient httpClient;

	private final ObjectMapper objectMapper;

	/**
	 * Constructor for HttpClientRequester.
	 * @param endpoint Base URL for the API endpoint.
	 * @param authorization Authentication object.
	 */
	public HttpClientRequester(String endpoint, TossPaymentsAuthentication authorization) {
		this(endpoint, authorization, DEFAULT_CONNECT_TIMEOUT);
	}

	/**
	 * Constructor for HttpClientRequester with custom connect timeout.
	 * @param endpoint Base URL for the API endpoint.
	 * @param authorization Authentication object.
	 * @param connectTimeout Connection timeout in seconds.
	 */
	public HttpClientRequester(String endpoint, TossPaymentsAuthentication authorization, Integer connectTimeout) {
		this.endpoint = endpoint;
		this.defaultHeaders = getDefaultHeaders(authorization.getSecretKey());
		this.httpClient = HttpClient.newBuilder()
			.connectTimeout(Duration.ofSeconds(connectTimeout))
			.build();

		this.objectMapper = new ObjectMapper().setVisibility(
			PropertyAccessor.FIELD,
			JsonAutoDetect.Visibility.ANY
		);
	}


	/**
	 * Sends a GET request to the specified path.
	 * @param path The API endpoint path.
	 * @param responseType The class type to parse the response into.
	 */
	@Override
	public <T> T get(String path, Class<T> responseType) {
		try {
			return getAsync(path, responseType).get();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt(); // Restore interrupted status
			throw new RuntimeException("Request was interrupted", e);
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Sends a GET request to the specified path asynchronously.
	 * @param path The API endpoint path.
	 * @param responseType The class type to parse the response into.
	 */
	@Override
	public <T> CompletableFuture<T> getAsync(String path, Class<T> responseType) {
		HttpRequest request = createRequestBuilder(path)
			.GET()
			.build();

		return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
			.thenApply(response -> parseResponse(response, responseType));
	}

	/**
	 * Sends a POST request to the specified path with the given body.
	 *
	 * @param path The API endpoint path.
	 * @param body The request body to send.
	 * @param responseType The class type to parse the response into.
	 */
	@Override
	public <T> T post(String path, Object body, Class<T> responseType) {
		try {
			return postAsync(path, body, responseType).get();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException("Request was interrupted", e);
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Sends a POST request to the specified path with the given body asynchronously.
	 *
	 * @param path The API endpoint path.
	 * @param body The request body to send.
	 * @param responseType The class type to parse the response into.
	 */
	@Override
	public <T> CompletableFuture<T> postAsync(String path, Object body, Class<T> responseType) {
		try {
			String stringBody = objectMapper.writeValueAsString(body);
			HttpRequest request = createRequestBuilder(path)
				.POST(HttpRequest.BodyPublishers.ofString(stringBody))
				.build();

			return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
				.thenApply(response -> parseResponse(response, responseType));
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Failed to parse response body", e);
		}
	}

	/**
	 * Creates a new HttpRequest.Builder with the default headers and the specified path.
	 * @param path The API endpoint path.
	 * @return A new HttpRequest.Builder instance.
	 */
	private HttpRequest.Builder createRequestBuilder(String path) {
		HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(endpoint + path));
		defaultHeaders.forEach(builder::header);

		return builder;
	}

	/**
	 * Parses the HttpResponse and returns the response body parsed into the specified type.
	 * @param response The HttpResponse to parse.
	 * @param responseType The class type to parse the response into.
	 * @return The parsed response body.
	 */
	private <T> T parseResponse(HttpResponse<String> response, Class<T> responseType) {
		if (300 <= response.statusCode()) { // 100 ~ 199 status codes are not delivered from http client
			throw new TossApiException(response.statusCode());
		}

		try {
			return objectMapper.readValue(response.body(), responseType);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Failed to parse response body", e);
		}
	}

}
