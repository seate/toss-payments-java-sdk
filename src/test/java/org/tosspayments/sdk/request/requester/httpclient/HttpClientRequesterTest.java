package org.tosspayments.sdk.request.requester.httpclient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.any;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.http.HttpClient;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.eclipse.jetty.http.HttpStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.tosspayments.sdk.exception.TossApiException;
import org.tosspayments.sdk.util.toss.TossApiMockTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.common.Json;

/**
 * Tests for {@link HttpClientRequester}.
 */
class HttpClientRequesterTest extends TossApiMockTest {

	private static final String TEST_ENDPOINT = "/api/test";

	private static final String TEST_BODY = "{\"message\":\"Hello, World!\"}";

	private HttpClientRequester requester;

	/**
	 * Initializes the {@link HttpClientRequester} before each test.
	 */
	@BeforeEach
	void setUp() {
		requester = new HttpClientRequester(getEndpoint(), getAuthorization());
	}

	/**
	 * Test for a simple GET request.
	 */
	@Test
	void getSuccessTest() {
		wireMockServer.stubFor(get(urlEqualTo(TEST_ENDPOINT))
			.willReturn(aResponse()
				.withStatus(HttpStatus.OK_200)
				.withBody(TEST_BODY)
			)
		);

		Object response = requester.get(TEST_ENDPOINT, Object.class);
		assertNotNull(response);
	}

	/**
	 * Test for a simple GET request when failure by bad request status.
	 */
	@Test
	void getBadRequestStatusResponseTest() {
		wireMockServer.stubFor(get(urlEqualTo(TEST_ENDPOINT))
			.willReturn(aResponse()
				.withStatus(HttpStatus.BAD_REQUEST_400)
				.withBody(TEST_BODY)
			)
		);

		assertThrows(
			RuntimeException.class,
			() -> requester.get(TEST_ENDPOINT, Object.class)
		);
	}

	/**
	 * Test for a simple GET request when failure by .
	 */
	@Test
	void getFailureStatusResponseTest() {
		wireMockServer.stubFor(get(urlEqualTo(TEST_ENDPOINT))
			.willReturn(aResponse()
				.withStatus(HttpStatus.PROCESSING_102)
				.withBody(TEST_BODY)
			)
		);

		assertThrows(
			RuntimeException.class,
			() -> requester.get(TEST_ENDPOINT, Object.class)
		);
	}

	/**
	 * Test for a simple GET request when failure by IOException.
	 */
	@Test
	void getFailureByIoExceptionTest() {
		mockSendAsyncErroredHttpClient(new ExecutionException("custom exception message", new RuntimeException()));

		assertThrows(
			RuntimeException.class,
			() -> requester.get(TEST_ENDPOINT, Object.class)
		);
	}

	/**
	 * Test for a simple GET request when failure by Interrupted.
	 */
	@Test
	void getFailureByInterruptedExceptionTest() {
		mockSendAsyncErroredHttpClient(InterruptedException.class);

		assertThrows(
			RuntimeException.class,
			() -> requester.get(TEST_ENDPOINT, Object.class)
		);
	}

	/**
	 * Test for an asynchronous GET request.
	 *
	 * @throws Exception if unexpected error occurs.
	 */
	@Test
	void getAsyncSuccessTest() throws Exception {
		wireMockServer.stubFor(get(urlEqualTo(TEST_ENDPOINT))
			.willReturn(aResponse()
				.withStatus(HttpStatus.OK_200)
				.withBody(TEST_BODY)
			)
		);

		Object response = requester.getAsync(TEST_ENDPOINT, Object.class).get();
		assertNotNull(response);
	}

	/**
	 * Test for an asynchronous GET request when failure.
	 */
	@Test
	void getAsyncFailureResponseTest() {
		wireMockServer.stubFor(get(urlEqualTo(TEST_ENDPOINT))
			.willReturn(aResponse()
				.withStatus(HttpStatus.BAD_REQUEST_400)
				.withBody(TEST_BODY)
			)
		);

		ExecutionException exception = assertThrows(
			ExecutionException.class,
			() -> requester.getAsync(TEST_ENDPOINT, Object.class).get()
		);

		Assertions.assertInstanceOf(
			TossApiException.class,
			exception.getCause(),
			"Expected a TossApiException as the cause"
		);
	}

	/**
	 * Test for a POST request.
	 */
	@Test
	void postSuccessTest() {
		wireMockServer.stubFor(post(urlEqualTo(TEST_ENDPOINT))
			.willReturn(aResponse()
				.withStatus(HttpStatus.OK_200)
				.withBody(TEST_BODY)
			)
		);

		Object response = requester.post(
			TEST_ENDPOINT,
			Json.write(TEST_BODY, Object.class),
			Object.class
		);
		assertNotNull(response);
	}

	/**
	 * Test for a POST request when failure.
	 */
	@Test
	void postFailureTest() {
		wireMockServer.stubFor(post(urlEqualTo(TEST_ENDPOINT))
			.willReturn(aResponse()
				.withStatus(HttpStatus.BAD_REQUEST_400)
				.withBody(TEST_BODY)
			)
		);

		assertThrows(
			RuntimeException.class,
			() -> requester.post(
				TEST_ENDPOINT,
				Json.write(TEST_BODY, Object.class),
				Object.class
			)
		);
	}

	/**
	 * Test for a simple GET request when failure by IOException.
	 */
	@Test
	void postFailureByIoExceptionTest() {
		mockSendAsyncErroredHttpClient(IOException.class);

		assertThrows(
			RuntimeException.class,
			() -> requester.post(
				TEST_ENDPOINT,
				Json.write(TEST_BODY, Object.class),
				Object.class
			)
		);
	}

	/**
	 * Test for a simple GET request when failure by Interrupted.
	 */
	@Test
	void postFailureByInterruptedExceptionTest() {
		mockSendAsyncErroredHttpClient(InterruptedException.class);

		assertThrows(
			RuntimeException.class,
			() -> requester.post(
				TEST_ENDPOINT,
				Json.write(TEST_BODY, Object.class),
				Object.class
			)
		);
	}

	/**
	 * Test for an asynchronous POST request.
	 *
	 * @throws ExecutionException if the request fails exceptionally.
	 * @throws InterruptedException if the request is interrupted.
	 */
	@Test
	void postAsyncSuccessTest() throws ExecutionException, InterruptedException {
		wireMockServer.stubFor(post(urlEqualTo(TEST_ENDPOINT))
			.willReturn(aResponse()
				.withStatus(HttpStatus.OK_200)
				.withBody(TEST_BODY)
			)
		);

		Object response = requester.postAsync(
			TEST_ENDPOINT,
			Json.write(TEST_BODY, Object.class),
			Object.class
		).get();
		assertNotNull(response);
	}

	/**
	 * Test for an asynchronous POST request when failure.
	 */
	@Test
	void postAsyncFailureResponseTest() {
		wireMockServer.stubFor(post(urlEqualTo(TEST_ENDPOINT))
			.willReturn(aResponse()
				.withStatus(HttpStatus.BAD_REQUEST_400)
				.withBody(TEST_BODY)
			)
		);

		ExecutionException exception = assertThrows(
			ExecutionException.class,
			() -> requester.postAsync(
				TEST_ENDPOINT,
				Json.write(TEST_BODY, Object.class),
				Object.class
			).get()
		);

		Assertions.assertInstanceOf(
			RuntimeException.class,
			exception.getCause(),
			"Expected a RuntimeException as the cause"
		);
	}

	/**
	 * Test for an asynchronous POST request when failure.
	 */
	@Test
	void postAsyncFailureByJsonProcessingExceptionTest() {
		mockWriteValueAsStringErroredObjectMapper(JsonProcessingException.class);

		assertThrows(
			RuntimeException.class,
			() -> requester.postAsync(
				TEST_ENDPOINT,
				Json.write(TEST_BODY, Object.class),
				Object.class
			).get()
		);
	}

	/**
	 * Test for parsing the response body when failure.
	 */
	@Test
	void responseParsingFailureTest() {
		wireMockServer.stubFor(get(urlEqualTo(TEST_ENDPOINT))
			.willReturn(aResponse()
				.withStatus(HttpStatus.OK_200)
				.withBody(TEST_BODY)
			)
		);

		mockReadValueErroredObjectMapper(JsonProcessingException.class);

		ExecutionException exception = assertThrows(
			ExecutionException.class,
			() -> requester.getAsync(TEST_ENDPOINT, Object.class).get()
		);

		Assertions.assertInstanceOf(
			RuntimeException.class,
			exception.getCause(),
			"Expected a RuntimeException as the cause"
		);
	}

	/**
	 * Test for request body when failure by InterruptedException.
	 */
	@Test
	void getThrowsInterruptedException() {
		// 1) spy 생성
		HttpClientRequester spyReq = spy(requester);

		// 2) getAsync(...)이 반환할 Future를 만드는데,
		//    get() 호출 시 InterruptedException을 던지도록 override
		CompletableFuture<Object> badFuture = new CompletableFuture<>() {
			@Override
			public Object get() throws InterruptedException {
				throw new InterruptedException("simulated interrupt");
			}
		};

		// 3) spy의 getAsync를 스텁
		doReturn(badFuture)
			.when(spyReq).getAsync(anyString(), any());

		// 4) get() 호출 → InterruptedException 잡아서 RuntimeException으로 래핑
		RuntimeException ex = assertThrows(RuntimeException.class, () ->
			spyReq.get("/foo", Object.class)
		);
		Assertions.assertTrue(ex.getMessage().contains("Request was interrupted"));
		Assertions.assertInstanceOf(InterruptedException.class, ex.getCause());
	}

	/**
	 * Test for request body when failure by InterruptedException.
	 */
	@Test
	void postThrowsInterruptedException() {
		HttpClientRequester spyReq = spy(requester);

		CompletableFuture<Object> badFuture = new CompletableFuture<>() {
			@Override
			public Object get() throws InterruptedException {
				throw new InterruptedException("simulated interrupt");
			}
		};

		doReturn(badFuture)
			.when(spyReq).postAsync(anyString(), any(), any());

		RuntimeException ex = assertThrows(RuntimeException.class, () ->
			spyReq.post("/foo", Map.of("a", "b"), Object.class)
		);
		Assertions.assertTrue(ex.getMessage().contains("Request was interrupted"));
		Assertions.assertInstanceOf(InterruptedException.class, ex.getCause());
	}

	/**
	 * Makes the HTTP client's sendAsync method throw an error of the specified type.
	 *
	 * @param exceptionType the type of exception to throw.
	 * @param <E> the type of exception.
	 */
	private <E extends Throwable> void mockSendAsyncErroredHttpClient(Class<E> exceptionType) {
		try {
			Constructor<E> exceptionConstructor = exceptionType
				.getDeclaredConstructor(String.class);
			exceptionConstructor.setAccessible(true);
			E exception = exceptionConstructor.newInstance("custom exception message");

			mockSendAsyncErroredHttpClient(exception);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Makes the HTTP client's sendAsync method throw an error.
	 *
	 * @param exception the exception to throw.
	 * @param <E> the type of exception.
	 */
	private <E extends Throwable> void mockSendAsyncErroredHttpClient(E exception) {
		HttpClient mockHttpClient = mock(HttpClient.class);
		Mockito.when(mockHttpClient.sendAsync(any(), any())).thenReturn(CompletableFuture.failedFuture(exception));

		try {
			Field httpClientField = this.requester.getClass().getDeclaredField("httpClient");
			httpClientField.setAccessible(true);
			httpClientField.set(this.requester, mockHttpClient);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Makes the ObjectMapper's writeValueAsString method throw an error of the specified type.
	 *
	 * @param exceptionType the type of exception to throw.
	 * @param <E> the type of exception.
	 */
	private <E extends Throwable> void mockWriteValueAsStringErroredObjectMapper(Class<E> exceptionType) {
		ObjectMapper mockObjectMapper = mock(ObjectMapper.class);
		try {
			Constructor<E> exceptionConstructor = exceptionType
				.getDeclaredConstructor(String.class);
			exceptionConstructor.setAccessible(true);
			E exception = exceptionConstructor.newInstance("custom exception message");

			when(mockObjectMapper.writeValueAsString(any())).thenThrow(exception);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		try {
			Field objectMapperField = this.requester.getClass().getDeclaredField("objectMapper");
			objectMapperField.setAccessible(true);
			objectMapperField.set(this.requester, mockObjectMapper);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Makes the ObjectMapper's readValue method throw an error of the specified type.
	 *
	 * @param exceptionType the type of exception to throw.
	 * @param <E> the type of exception.
	 */
	private <E extends Throwable> void mockReadValueErroredObjectMapper(Class<E> exceptionType) {
		ObjectMapper mockObjectMapper = mock(ObjectMapper.class);
		try {
			Constructor<E> exceptionConstructor = exceptionType
				.getDeclaredConstructor(String.class);
			exceptionConstructor.setAccessible(true);
			E exception = exceptionConstructor.newInstance("custom exception message");

			doThrow(exception).when(mockObjectMapper).readValue(anyString(), ArgumentMatchers.<Class<Object>>any());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		try {
			Field objectMapperField = this.requester.getClass().getDeclaredField("objectMapper");
			objectMapperField.setAccessible(true);
			objectMapperField.set(this.requester, mockObjectMapper);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}


}
