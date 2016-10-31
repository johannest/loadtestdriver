LoadTestDriver
==============
WebDriver for recording a Gatling load test, from an existing TestBench test.
Probably the easiest way to quickly record a scalability/load test for a Vaadin web application


Workflow
========
* Install PhantomJS (>=2.0.0) to your computer, verify that it is your path by executing: `phantomjs -v`on the command prompt


* Add dependency to your pom.xml:
```
<dependency>
	<groupId>org.vaadin.johannest</groupId>
	<artifactId>loadtestdriver</artifactId>
	<version>0.0.1</version>
</dependency> 
```

* Use LoadTestDriver instead of e.g. ChromeDriver in your TestBench test's setup method:
```
@Before
public void setUp() throws Exception {
	WebDriver driver = new LoadTestDriverBuilder().
			withIpAddress(LoadTestDriver.getLocalIpAddress()).
			withNumberOfConcurrentUsers(1).
			withRampUpTimeInSeconds(1).
			withTestName("MyUI_ScalabilityTest").
			withPath("/Users/jotatu/Desktop/gatling").
			withResourcesPath("/Users/jotatu/Desktop/gatling").
			withStaticResourcesIngnoring().
			withTestRefactoring().
			build();
	setDriver(driver);
//		setDriver(new ChromeDriver());	
}
```

* Configure your TestBench test to open the application to be tested with your ip address:
```
private void openTestUrl() {
	// opens URL http://your.local.ip.address:8080/ui
    getDriver().get(LoadTestDriver.getLocalIpAddressWithPortAndContextPath(8080,"ui"));
}
```

* Run the test as a JUnit test: LoadTestDriver uses Gatling to record the load test with parameters given in Driver setup (see above), test is saved in the given destination (see above).
