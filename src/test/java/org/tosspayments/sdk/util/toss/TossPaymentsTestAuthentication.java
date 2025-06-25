package org.tosspayments.sdk.util.toss;

import org.tosspayments.sdk.authorization.TossPaymentsAuthentication;

/**
 * This class is used to authorize requests to the Toss Payments API in a test environment.
 * It extends the TossPaymentsAuthorization class and uses the secret key from TossTestInform.
 */
public class TossPaymentsTestAuthentication extends TossPaymentsAuthentication {

	/**
	 * Constructor that initializes the TossPaymentsTest with the secret key from TossTestInform.
	 */
	public TossPaymentsTestAuthentication() {
		super(TossTestInform.getSecret());
	}

}
