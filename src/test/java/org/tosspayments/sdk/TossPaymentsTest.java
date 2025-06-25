package org.tosspayments.sdk;


import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.tosspayments.sdk.request.requester.Requester;
import org.tosspayments.sdk.util.toss.TossTestInform;

/**
 * Tests for {@link TossPayments}.
 */
class TossPaymentsTest {


	/**
	 * Tests the initialization of {@link TossPayments} with a secret key.
	 * This test ensures that the TossPayments instance can be created without exceptions.
	 */
	@Test
	void tossPaymentsInitializationTest() {
		TossPayments tossPayments = new TossPayments(TossTestInform.getSecret());

		Requester mockRequester = mock(Requester.class);
		TossPayments tossPaymentsWithCustomRequester = new TossPayments(TossTestInform.getSecret(), mockRequester);
	}
}
