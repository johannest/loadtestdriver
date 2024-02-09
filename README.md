[![Published on Vaadin  Directory](https://img.shields.io/badge/Vaadin%20Directory-published-00b4f0.svg)](https://vaadin.com/directory/component/loadtestdriver-add-on)
[![Stars on Vaadin Directory](https://img.shields.io/vaadin-directory/star/loadtestdriver-add-on.svg)](https://vaadin.com/directory/component/loadtestdriver-add-on)

LoadTestDriver
==============
WebDriver for recording a Gatling load test, from an existing TestBench test.
Probably the easiest way to quickly record a scalability/load test for a Vaadin web application.

It has also a [CLI](#LoadTest-CLI) version for preparing pre-recorder Gatling Scala test scrips.

Note
====
This is the README of Vaadin 10+ version. See master branch for Vaadin 7 version.

Test refecatoring feature does not yet work for this version!

Supported Gatling versions
====

| LoadTestDriver version | Vaadin version | GatlingVersion | Script file format |
|------------------------|-----------|-----------------|--------------------|
| 0.1                    | V7        | 3.0.x          | Scala              |
| 0.2                    | V8        | 3.0.x          | Scala              |
| 0.3                    | V14       | 3.0.x          | Scala              |
| 0.5                    | V23       | 3.10.x         | Java11             |
| 0.6+                   | V24+      | 3.10.x         | Java11             |


## Workflow
### Install latest ChromeDriver to your computer: 

http://chromedriver.chromium.org

### Add dependency to your pom.xml

```xml
<dependency>
	<groupId>org.vaadin.johannest</groupId>
	<artifactId>loadtestdriver</artifactId>
	<version>0.6.0</version>
    <scope>test</scope>
</dependency> 
```

### Use LoadTestDriver instead of e.g. ChromeDriver in your TestBench test's setup method. It recommended to store reference to the driver instance to make it easy to access the API of the driver.
```java
@BeforeEach
public void setUp() throws Exception {
	WebDriver driver = new LoadTestDriverBuilder().
			withIpAddress(LoadTestDriver.getLocalIpAddress()).
			withNumberOfConcurrentUsers(1).
			withRampUpTimeInSeconds(1).
			withTestName("MyUI_ScalabilityTest").
			withPath("/Users/your_username/Desktop/gatling").
			withResourcesPath("/Users/your_username/Desktop/gatling").
			withStaticResourcesIngnoring().
			withTestRefactoring().
			build();
	setDriver(driver);
    driver.get("http://your-ip-address:8080/");
}
```

### Verify that the logged in user has write access to the directory(ies) specified above TestBench test setup.
LoadTestDriver stores a recorded scala script by default in the System's tmp-folder, but you can specify used output folder like in above example. Make sure that you have a write access to that folder.
**Note: Make sure you only run one test one time for the given directory and test name, othervice the resulting script might be broken.**

### Configure your TestBench test to open the application to be tested with your ip address:
```java
private void openTestUrl() {
	// opens URL http://your.local.ip.address:8080/ui
    getDriver().get(LoadTestDriver.getLocalIpAddressWithPortAndContextPath(8080,"ui"));
}
```
**Note: The local IP address API above might not always work! In that case you have to explicitly hard code your ip v4 address into the test**

### Run the test as a JUnit test
LoadTestDriver uses Gatling to record the load test with parameters given in Driver setup (see above), test is saved in the given destination (see above).

## LoadTest CLI

### Workflow

Build CLI with the following command `mvn clean package spring-boot:repackage` This will create an executable jar file from the project.

To run the CLI you can use `java -jar` for example `java -jar ./target/loadtestdriver-0.5.1.jar -f ./test_scripts/LoginTest.scala -r ./test_scripts/resources`

To see all possible command line options run `java -jar ./target/loadtestdriver-0.5.1.jar --help`

### Recorder
LoadTest CLI can be used also to quickly start Gatling recorder with the preferred recording parameters. Run `java -jar ./target/loadtestdriver-0.5.1.jar record --help` to see possible command line options.

### Quick start guide with Vaadin BookStore demo application
1. Open Firefox and its developer tools (F12).
2. Select Network tab and click "Disable cache".
3. From the cog symbol select "Persist log".
4. Paste demo app's URL (https://vaadin-bookstore-example.demo.vaadin.com/) to URL bar.
5. Login to app and do other UI interactions if wanted.
6. When ready, select "Save All As HAR" from the cog symbol (save as `vaadin_bookstore.har` under `./src/test/resources`)
7. Run `java -jar ./target/loadtestdriver-0.5.1.jar record -hf ./src/test/resources/vaadin_bookstore.har -d ./src/test/java/ -r ./src/test/resources` to generate Gatling test script in headless mode from a HAR file.
8. Run `java -jar ./target/loadtestdriver-0.5.1.jar -f ./src/test/java/vaadin_bookstore -r ./src/test/resources` to convert the test script to Vaadin compatible format.
9. Run `mvn -Pscalability gatling:test` to compile and run the test script with Gatling. 
