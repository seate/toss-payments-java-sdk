package org.tosspayments.sdk.util.wiremock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to exclude specific keys from being used in Wiremock tests.
 * This can be useful when you want to avoid using certain keys in your test cases.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExcludeWiremockStubBodyFields {

	/**
	 * An array of keys to be excluded from the Wiremock stub body.
	 * These keys will not be considered when matching requests in Wiremock tests.
	 *
	 * @return an array of keys to exclude
	 */
	String[] value() default {};
}
