package org.tosspayments.sdk.util.wiremock;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.TestInfo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.common.Json;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.StubMappingTransformer;
import com.github.tomakehurst.wiremock.matching.ContentPattern;
import com.github.tomakehurst.wiremock.matching.EqualToJsonPattern;
import com.github.tomakehurst.wiremock.matching.MatchesJsonPathPattern;
import com.github.tomakehurst.wiremock.matching.RequestPattern;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;

/**
 * WiremockRecordingTransformer is a custom Wiremock transformer that modifies the request patterns
 * in Wiremock stubs by replacing EqualToJson patterns with MatchesJsonPath patterns.
 * It excludes specified fields from the stub body based on the test method's annotations.
 */
public class WiremockRecordingTransformer extends StubMappingTransformer {

	private static final ObjectMapper MAPPER = new ObjectMapper();

	private final Set<String> excludedKeys;

	/**
	 * Constructor for WiremockRecordingTransformer.
	 * @param testInfo Test information containing the test class and method names.
	 */
	public WiremockRecordingTransformer(TestInfo testInfo) {
		this.excludedKeys = getExcludedStubBodyFields(testInfo);
	}

	/**
	 * Get the set of fields to be excluded from the Wiremock stub body.
	 * @param testInfo Test information containing the test class and method names.
	 * @return Set of field names to be excluded from the Wiremock stub body.
	 */
	private Set<String> getExcludedStubBodyFields(TestInfo testInfo) {
		Method testMethod = testInfo.getTestMethod()
			.orElseThrow(() -> new IllegalStateException("Test method is not present in TestInfo"));

		if (testMethod.isAnnotationPresent(ExcludeWiremockStubBodyFields.class)) {
			return Set.of(testMethod.getAnnotation(ExcludeWiremockStubBodyFields.class).value());
		}

		return Set.of();
	}

	/**
	 * Copy the original RequestPattern using Jackson's ObjectMapper.
	 * This is necessary to avoid issues with reflection when modifying the bodyPatterns field.
	 * @param mapping The original StubMapping containing the RequestPattern to be copied.
	 * @param files The FileSource for the Wiremock server. not used in this case.
	 * @param parameters Parameters for the transformation. not used in this case.
	 * @return A new RequestPattern that is a copy of the original RequestPattern with modified body patterns.
	 */
	@Override
	public StubMapping transform(StubMapping mapping, FileSource files, Parameters parameters) {
		RequestPattern originalRequestPattern = mapping.getRequest();
		List<ContentPattern<?>> original = originalRequestPattern.getBodyPatterns();
		if (original == null) {
			return mapping;
		}

		List<ContentPattern<?>> newPatterns = new ArrayList<>();
		for (ContentPattern<?> pattern : original) {
			// Convert only equalToJson Patterns
			if (pattern instanceof EqualToJsonPattern eqJson) {
				try {
					JsonNode root = MAPPER.readTree(eqJson.getExpected());

					root.fieldNames().forEachRemaining(field -> {
						if (excludedKeys.contains(field)) {
							return;
						}

						JsonNode value = root.get(field);
						String literal = (value.isNumber() || value.isBoolean())
							? value.toString()
							: "'" + value.asText() + "'";
						String expr = "$[?(@" + "." + field + " == " + literal + ")]";

						newPatterns.add(new MatchesJsonPathPattern(expr));
					});
				} catch (JsonProcessingException e) {
					throw new RuntimeException(e);
				}
			} else { // For other patterns, we keep them as they are
				newPatterns.add(pattern);
			}
		}

		StubMapping newMapping = StubMapping.buildFrom(mapping.toString());
		RequestPattern newRequestPattern = setBodyPatterns(originalRequestPattern, newPatterns);

		newMapping.setRequest(newRequestPattern);

		return newMapping;
	}

	/**
	 * Set the body patterns of the RequestPattern.
	 * @param original The original RequestPattern to be modified.
	 * @param newPatterns The new list of ContentPattern to be set as body patterns.
	 * @return A new RequestPattern with the updated body patterns.
	 */
	private RequestPattern setBodyPatterns(RequestPattern original, List<ContentPattern<?>> newPatterns) {
		try {
			RequestPattern newRequestPattern = copy(original);

			Field bodyPatternsField = RequestPattern.class.getDeclaredField("bodyPatterns");
			bodyPatternsField.setAccessible(true);
			bodyPatternsField.set(newRequestPattern, newPatterns);
			bodyPatternsField.setAccessible(false);

			return newRequestPattern;
		} catch (NoSuchFieldException e) {
			throw new RuntimeException("Failed to get field when using reflection in wiremock stub transforming {}", e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(
				"Failed to access field when using reflection in wiremock stub transforming {}", e
			);
		}
	}

	/**
	 * Copy the original RequestPattern.
	 * @param original The original RequestPattern to be copied.
	 * @return A new RequestPattern that is a copy of the original.
	 */
	private RequestPattern copy(RequestPattern original) {
		try {
			return MAPPER.readValue(
				Json.write(original),
				RequestPattern.class
			);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Unknown error occurred while parsing Request Pattern {}", e);
		}
	}

	/**
	 * Get the name of this transformer.
	 * @return The simple name of this class.
	 */
	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	/**
	 * Check if this transformer applies globally.
	 * @return true, indicating that this transformer should be applied globally.
	 */
	@Override
	public boolean applyGlobally() {
		return true;
	}
}
