package org.tosspayments.sdk.util.toss;

import org.tosspayments.sdk.authorization.TossPaymentsAuthentication;
import org.tosspayments.sdk.util.wiremock.WiremockTest;

/**
 * This class is used to mock the Toss Payments API for testing purposes.
 * It extends the WireMockTest class to provide a mock server for API requests.
 */
public class TossApiMockTest extends WiremockTest {

	/**
	 * Returns Toss Authentication for the mock server.
	 *
	 * @return The base URL as a string.
	 */
	protected TossPaymentsAuthentication getAuthorization() {
		return new TossPaymentsTestAuthentication();
	}
}
