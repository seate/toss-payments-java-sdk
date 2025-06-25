## 🙌 Contributing to TossPayments Java SDK

Thanks for your interest in contributing! By participating, you agree to follow this guide.

---

### 📋 Table of Contents

* [Prerequisites](#-prerequisites)
* [Code Style & Linting](#-code-style--linting)
* [Commit Message Guidelines](#-commit-message-guidelines)
* [Testing & Coverage](#-testing--coverage)
* [Javadoc Validation](#-javadoc-validation)
* [Continuous Integration](#-continuous-integration)
* [Pull Request Process](#-pull-request-process)

---

## 📌 Prerequisites

* Java 17
* Gradle 8.10
* Git

---

## 💅 Code Style & Linting

We enforce [Checkstyle](https://checkstyle.org/) rules (based on Naver’s conventions) for both `src/main/java` and `src/test/java`.

* **Configuration file**: `config/checkstyle/naver-checkstyle-rules.xml`
* **Suppressions**: `config/checkstyle/naver-checkstyle-suppressions.xml`

**Run lint** manually:

Only Production code
```bash
./gradlew checkstyleMain
```

Only Test code
```bash
./gradlew checkstyleTest
```


All code
```bash
./gradlew check
```

The build fails on any warnings or errors (`ignoreFailures = false`, `maxWarnings = 0`).

---

## 📝 Commit Message Guidelines

We follow a **Conventional Commits** style with **PascalCase** types.

* **Allowed types**:

    * `Build`, `Chore`, `Ci`, `Docs`, `Feat`, `Fix`, `Perf`, `Refactor`, `Revert`, `Style`, `Test`, `Merge`, `Init`
* **Format**:

  ```
  Type(scope)?(!)?: short description (≤50 chars)

  [optional body lines (≤72 chars each)]

  [optional FOOTER: key: value]
  ```

`commit-msg` git hook applies when refreshing the gradle.

---

## 🧪 Testing & Coverage

We use **Jacoco** for test coverage.

* **Run tests**:

  ```bash
  ./gradlew test
  ```

* **Generate coverage report**:

    ```bash
    ./gradlew jacocoTestReport
    ```
    
  * HTML report: `build/reports/jacoco/test/html/index.html`

<br>

* **Coverage thresholds** (verified by `jacocoTestCoverageVerification`):

    * **Overall instructions**: ≥30%
    * **Branch coverage**: ≥90%
    * **Line coverage**: ≥80%

To run tests + coverage checks in one go:

```bash
./gradlew testCoverage
```

---

## 📚 Javadoc Validation

We require Javadoc on `src/main/java` and **package-level** Javadoc on tests.

* **Generate API docs**:

  ```bash
  ./gradlew javadoc
  ```

    * Output: `docs/`

* **Validate test Javadoc** (no output files):

  ```bash
  ./gradlew testJavadocTest
  ```

To run all docs checks:

```bash
./gradlew totalJavadocTest
```

---

## 🚀 Continuous Integration

The `totalCITest` task aggregates all checks:

```bash
./gradlew totalCITest
```

It runs: lint, tests, coverage, Javadoc, and Git hook validations.

---

## 🔄 Pull Request Process

1. **Branch** off `develop`:

   ```bash
   git checkout -b feat/your-feature
   ```
2. **Implement** your changes.
3. **Run** all checks locally:

   ```bash
   ./gradlew totalCITest
   ```
4. **Commit** following guidelines.
5. **Push** and open a **PR** against `develop`.
6. **Await** review and address feedback.

Thanks for contributing! 🎉
