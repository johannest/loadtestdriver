LoadTestDriver
==============
WebDriver for recording a Gatling load test, from an existing TestBench test.
Probably the easiest way to quickly record a scalability/load test for a Vaadin web application

Note
====
This is the README of Vaadin 10+ version. See master branch for Vaadin 7 version.

Test refecatoring feature does not yet work for this version!


Workflow
========
### Install latest ChromeDriver to your computer: 

http://chromedriver.chromium.org

### Add dependency to your pom.xml

```
<dependency>
	<groupId>org.vaadin.johannest</groupId>
	<artifactId>loadtestdriver</artifactId>
	<version>0.3.0</version>
</dependency> 
```

### Allow remote access of your application server 
Verify that your application server works in your browser with your local IP address such as 192.168.12.3:8080. It is **not** enough that it works in `localhost:8080` or `127.0.0.1:8080`. For example, in case of WildFly you can use command line parameter `-b 0.0.0.0` (https://bgasparotto.com/enable-wildfly-remote-access/)

### Use LoadTestDriver instead of e.g. ChromeDriver in your TestBench test's setup method
```
@Before
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
//		setDriver(new ChromeDriver());	
}
```

### Verify that the logged in user has write access to the directory(ies) specified above TestBench test setup.
LoadTestDriver stores a recorded scala script by default in the System's tmp-folder, but you can specify used output folder like in above example. Make sure that you have a write access to that folder.

### Configure your TestBench test to open the application to be tested with your ip address:
```
private void openTestUrl() {
	// opens URL http://your.local.ip.address:8080/ui
    getDriver().get(LoadTestDriver.getLocalIpAddressWithPortAndContextPath(8080,"ui"));
}
```

### Run the test as a JUnit test
LoadTestDriver uses Gatling to record the load test with parameters given in Driver setup (see above), test is saved in the given destination (see above).
