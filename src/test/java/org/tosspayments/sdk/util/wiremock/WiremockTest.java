package org.tosspayments.sdk.util.wiremock;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tosspayments.sdk.util.toss.TossTestInform;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

/**
 * Base class for WireMock tests.
 * This class sets up a WireMock server for testing purposes, allowing for request recording and stubbing.
 */
public abstract class WiremockTest {

	private static final String REAL_ENDPOINT = TossTestInform.ENDPOINT;

	private static final String RECORDING_ENV_VAR = "WIREMOCK_RECORDING";

	private static final String WIREMOCK_ENDPOINT = "http://localhost";

	private static final Integer WIREMOCK_PORT = 40401;

	private static final String STUBS_DIR = "src/test/resources/wiremock";

	private static final String STUBS_DIR_DEFAULT = "mappings";

	private static final Logger baseLog = LoggerFactory.getLogger(WiremockTest.class);


	/**
	 * Default logger when testing.
	 */
	protected final Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * Mock server
	 */
	protected WireMockServer wireMockServer;


	/**
	 * Returns the endpoint URL for the WireMock server.
	 * The endpoint is constructed using the `WIREMOCK_ENDPOINT` and `WIREMOCK_PORT` constants.
	 *
	 * @return The endpoint URL for the WireMock server.
	 */
	protected static String getEndpoint() {
		return WIREMOCK_ENDPOINT + ":" + WIREMOCK_PORT;
	}


	/**
	 * Sets up the WireMock server before each test.
	 * Decide recording by {@value RECORDING_ENV_VAR} environment variable.
	 * @param testInfo Test information containing the test class and method names.
	 */
	@BeforeEach
	protected void setupWireMock(TestInfo testInfo) {
		String testMethodStubDirectory = getTestMethodStubDirectory(testInfo);
		String stubMappingDirectory = testMethodStubDirectory + File.separator + STUBS_DIR_DEFAULT;
		createDirectoryIfNotExists(stubMappingDirectory);

		WireMockConfiguration wireMockConfiguration = WireMockConfiguration.options()
			.extensions(new WiremockRecordingTransformer(testInfo))
			.port(WIREMOCK_PORT)
			.usingFilesUnderDirectory(testMethodStubDirectory);

		wireMockServer = new WireMockServer(wireMockConfiguration);
		wireMockServer.start();

		if (isRecording()) {
			deleteFilesInDirectory(stubMappingDirectory);

			wireMockServer.startRecording(recordSpec()
				.forTarget(REAL_ENDPOINT)
				.makeStubsPersistent(true)
			);

			baseLog.info("WireMock is recording requests to {}", REAL_ENDPOINT);
		} else {
			baseLog.info("WireMock is using stubs from {}", STUBS_DIR);
		}
	}

	/**
	 * Returns the directory path for storing test method stubs.
	 * The directory is structured as: `src/test/resources/wiremock/{TestClassName}/{TestMethodName}`.
	 *
	 * @param testInfo Test information containing the test class and method names.
	 * @return The directory path for storing test method stubs.
	 */
	private String getTestMethodStubDirectory(TestInfo testInfo) {
		String testClassName = testInfo.getTestClass()
			.orElseThrow(() -> new IllegalStateException("Test class is not present in TestInfo"))
			.getSimpleName();

		String testMethodName = testInfo.getTestMethod()
			.orElseThrow(() -> new IllegalStateException("Test method is not present in TestInfo"))
			.getName();

		return STUBS_DIR + File.separator + testClassName + File.separator + testMethodName;
	}

	/**
	 * Creates the directory for storing test method stubs if it does not already exist.
	 * If the directory creation fails, it throws a RuntimeException.
	 *
	 * @param directory The directory path to create.
	 */
	private void createDirectoryIfNotExists(String directory) {
		Path directoryPath = Paths.get(directory);
		if (!Files.exists(directoryPath)) {
			try {
				Files.createDirectories(directoryPath);
			} catch (IOException e) {
				throw new RuntimeException("Failed to create directory: " + directory, e);
			}
		}
	}

	/**
	 * Deletes all files in the specified directory, except for the directory itself.
	 * If the directory does not exist, it does nothing.
	 * If an error occurs during deletion, it throws a RuntimeException.
	 *
	 * @param directory The directory path from which to delete files.
	 */
	private void deleteFilesInDirectory(String directory) {
		Path directoryPath = Paths.get(directory);
		if (Files.exists(directoryPath)) {
			try (Stream<Path> walk = Files.walk(directoryPath)) {
				walk
					.sorted(Comparator.reverseOrder())
					.filter(path -> !path.equals(directoryPath))
					.forEach(file -> {
						try {
							Files.deleteIfExists(file);
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
					});
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Stops the WireMock server after each test.
	 * If recording was enabled, it stops the recording and logs the action.
	 */
	@AfterEach
	protected void stopWireMock() {
		if (isRecording()) {
			wireMockServer.stopRecording();

			baseLog.info("WireMock stopped recording requests.");
		}

		try {
			wireMockServer.stop();

			baseLog.info("WireMock server stopped.");
		} catch (Exception e) {
			if (e instanceof InterruptedException) {
				Thread.currentThread().interrupt();

				baseLog.debug("WireMock server stop interrupted", e);
			} else {
				throw e;
			}
		}

	}

	/**
	 * Checks if the WireMock server is set to record requests.
	 * This is determined by the `WIREMOCK_RECORDING` environment variable.
	 *
	 * @return true if recording is enabled, false otherwise.
	 */
	private Boolean isRecording() {
		String recordingFlag = System.getenv(RECORDING_ENV_VAR);
		return (recordingFlag != null) && recordingFlag.equalsIgnoreCase(Boolean.TRUE.toString());
	}

}
