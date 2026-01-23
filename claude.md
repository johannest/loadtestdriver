# LoadTestDriver (v24)

A Vaadin add-on that records Gatling load tests from existing TestBench tests. It bridges functional UI testing and performance testing by capturing HTTP traffic during Selenium tests and generating Gatling Java scripts.

**Version:** 0.6.0
**Target:** Vaadin 24.3.3
**Java:** 17

## Project Structure

```
src/main/java/org/vaadin/johannest/loadtestdriver/
├── LoadTestDriver.java           # Main driver extending ChromeDriver
├── LoadTestDriverBuilder.java    # Builder pattern API for configuration
├── LoadTestConfigurator.java     # Post-processes Gatling scripts
├── LoadTestsRecorder.java        # Wraps Gatling proxy recorder
├── LoadTestRunner.java           # Compiles and runs tests
├── ConfigurationParameters.java  # Test configuration (users, rampup, etc.)
├── RecordingParameters.java      # Recording settings (port, HAR file, etc.)
└── CommandLineRunner.java        # CLI interface using PicoCLI

src/main/resources/org/vaadin/johannest/loadtestdriver/
├── loadtestdriver.properties     # Gatling regex templates
└── recorder.conf                 # Gatling recorder configuration

src/test/java/                    # JUnit tests
src/test/resources/               # HAR files, gatling.conf, test fixtures
```

## Tech Stack

- **Java 17** - Source and target compatibility
- **Gatling 3.10.3** - Load testing framework (Java 11 script format)
- **Selenium 4.17.0** - Browser automation via ChromeDriver
- **Vaadin TestBench 9.2.1** - UI testing framework
- **Jakarta Servlet 6.0.0** - Servlet API (migrated from javax)
- **PicoCLI 4.7.5** - Command-line interface
- **Maven** - Build system with Spring Boot plugin

## Build Commands

```bash
# Build and install
mvn clean install

# Run tests
mvn test

# Package without tests
mvn package -DskipTests

# Build with Gatling Highcharts (scalability profile)
mvn clean install -Pscalability
```

The build produces:
- `loadtestdriver-0.6.0.jar` - Library JAR
- `loadtestdriver-0.6.0-spring-boot.jar` - Standalone executable

## CLI Usage

The project includes a command-line interface via `CommandLineRunner`:

```bash
# Prepare/process recorded Gatling scripts (default command)
java -jar loadtestdriver-0.6.0-spring-boot.jar prepare [options]

# Start Gatling recorder
java -jar loadtestdriver-0.6.0-spring-boot.jar record [options]

# Run compiled test scripts
java -jar loadtestdriver-0.6.0-spring-boot.jar run [options]
```

Supports HAR file import for headless recording scenarios.

## Architecture

**Workflow:**

1. `LoadTestDriverBuilder` creates a configured `LoadTestDriver`
2. `LoadTestDriver.get(url)` starts `LoadTestsRecorder` (Gatling proxy)
3. Test execution captures HTTP traffic through the proxy
4. `LoadTestDriver.close()` stops recording and triggers `LoadTestConfigurator`
5. `LoadTestConfigurator` post-processes the generated Java script
6. `LoadTestRunner` compiles and executes the load test

**Key Classes:**

- `LoadTestDriver` - Extends ChromeDriver, manages recording lifecycle
- `LoadTestConfigurator` - Parses Vaadin UIDL responses, extracts connector IDs, replaces hardcoded values with variables
- `LoadTestsRecorder` - Wraps Gatling recorder, supports HAR file mode
- `CommandLineRunner` - PicoCLI-based CLI with subcommands

## Key Patterns

- **Builder Pattern**: `LoadTestDriverBuilder` provides fluent configuration
- **Property-based Extraction**: Multiple fallback strategies for connector ID extraction (cssId, id, label, placeholder, text, tag, theme)
- **HAR Import**: Alternative to proxy recording for CI/CD environments

## Code Conventions

- Package: `org.vaadin.johannest.loadtestdriver`
- Logging: `java.util.logging`
- JSON parsing: GWT Elemental library
- Constants: `UPPER_SNAKE_CASE`
- Script format: Gatling Java 11 DSL (not Scala)

## Testing

Tests in `src/test/java/org/vaadin/johannest/loadtestdriver/LoadTestConfiguratorTest.java`.

Test areas:
- UIDL response parsing (with/without HTML encoding)
- Connector mapping extraction
- Regex extractor generation
- Request body transformations
- End-to-end configuration workflow

Test resources:
- HAR files: `barista.har`, `vaadin_bookstore.har`
- Gatling config: `gatling.conf`
- Sample request/response fixtures

## Vaadin-Specific Details

Handles Vaadin 24 concerns:
- Connector ID extraction from UIDL responses using multiple strategies
- XSRF/CSRF token management
- syncId and clientId correlation
- Node ID to property mapping (cssId, id, label, placeholder, text, theme, tag)
- `ElFileBody` for templated request bodies

## Configuration Files

- `pom.xml` - Maven build, dependencies, Spring Boot plugin, scalability profile
- `loadtestdriver.properties` - Gatling Java DSL regex templates
- `recorder.conf` - Gatling proxy recorder settings
- `assembly/assembly.xml` - ZIP distribution packaging

## Builder Options

```java
new LoadTestDriverBuilder()
    .withIpAddress("192.168.1.x")
    .withNumberOfConcurrentUsers(10)
    .withRampUpTimeInSeconds(5)
    .withTestName("MyTest")
    .withPath("/path/to/gatling")
    .withResourcesPath("/path/to/resources")
    .withHeadlessEnabled(true)
    .withBrowserLanguageCode("en")
    .build();
```
