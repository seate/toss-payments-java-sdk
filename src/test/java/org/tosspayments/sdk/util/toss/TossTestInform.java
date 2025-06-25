package org.tosspayments.sdk.util.toss;

import org.tosspayments.sdk.TossPayments;

/**
 * Utility class to provide the Toss Inform endpoint and a test secret.
 * This is used for testing purposes and should not be used in production.
 */
public class TossTestInform {

	/**
	 * The endpoint for Toss when testing.
	 */
	public static final String ENDPOINT = TossPayments.ENDPOINT;

	private static final String TEST_SECRET_ENV_VAR = "TOSS_TEST_SECRET";

	// NOTE: This secret is not able to get payment results
	private static final String DEFAULT_TEST_SECRET = "test_sk_zXLkKEypNArWmo50nX3lmeaxYG5R";

	/**
	 * Retrieves the test secret from the environment variable or returns a default value.
	 *
	 * @return The test secret to be used for testing purposes.
	 */
	public static String getSecret() {
		String testSecret = System.getenv(TEST_SECRET_ENV_VAR);
		if (testSecret != null && !testSecret.isEmpty()) {
			return testSecret;
		} else {
			return DEFAULT_TEST_SECRET;
		}
	}
}
