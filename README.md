[![Published on Vaadin  Directory](https://img.shields.io/badge/Vaadin%20Directory-published-00b4f0.svg)](https://vaadin.com/directory/component/loadtestdriver-add-on)
[![Stars on Vaadin Directory](https://img.shields.io/vaadin-directory/star/loadtestdriver-add-on.svg)](https://vaadin.com/directory/component/loadtestdriver-add-on)

LoadTestDriver
==============
WebDriver for recording a Gatling load test, from an existing TestBench test.
Probably the easiest way to quickly record a scalability/load test for a Vaadin web application

Note! Latest development and fixes are currently done only for V24 and V23 branches (see the links to branches below). Feel free report an issue if you need fixes to older versions (v8 -- v14).

Note! V23 and V24 versions support alternative (CLI) approach of generating Gatling tests without a TestBench test: https://github.com/johannest/loadtestdriver/blob/v24/README.md#loadtest-cli

Workflow
========
### Add dependency to your pom.xml
[Vaadin 24](https://github.com/johannest/loadtestdriver/tree/v24)
```xml
<dependency>
	<groupId>org.vaadin.johannest</groupId>
	<artifactId>loadtestdriver</artifactId>
	<version>0.6.0</version>
	<scope>test</scope>
</dependency> 
```

[Vaadin 23](https://github.com/johannest/loadtestdriver/tree/v23)
```xml
<dependency>
	<groupId>org.vaadin.johannest</groupId>
	<artifactId>loadtestdriver</artifactId>
	<version>0.5.2</version>
	<scope>test</scope>
</dependency> 
```

[Vaadin 14](https://github.com/johannest/loadtestdriver/tree/v10)
```xml
<dependency>
	<groupId>org.vaadin.johannest</groupId>
	<artifactId>loadtestdriver</artifactId>
	<version>0.4.0</version>
	<scope>test</scope>
</dependency> 
```

Vaadin 8
```xml
<dependency>
	<groupId>org.vaadin.johannest</groupId>
	<artifactId>loadtestdriver</artifactId>
	<version>0.2.11</version>
	<scope>test</scope>
</dependency> 
```

### Allow remote access of your application server 
Verify that your application server works in your browser with your local IP address such as 192.168.12.3:8080. It is **not** enough that it works in `localhost:8080` or `127.0.0.1:8080` because of a limitation of PhantomJS. For example, in case of WildFly you can use command line parameter `-b 0.0.0.0` (https://bgasparotto.com/enable-wildfly-remote-access/)

### Use LoadTestDriver instead of e.g. ChromeDriver in your TestBench test's setup method. It recommended to store reference to the driver instance to make it easy to access the API of the driver.
```Java
@Before
public void setUp() throws Exception {
	    loadTestDriver = new LoadTestDriverBuilder().
    				withIpAddress("your-real-local-ipv4-address").
    				withNumberOfConcurrentUsers(2).
    				withRampUpTimeInSeconds(2).
    				withTestName("Bakery_AddOrder").
    				withPath("C:\\dev\\gatling8").
    				withResourcesPath("C:\\dev\\gatling8\\resources").
    				withStaticResourcesIngnoring().
    				withTestRefactoring().
    				build();
    		setDriver(loadTestDriver);
//		setDriver(new ChromeDriver());	
}
```

### Verify that the logged in user has write access to the directory(ies) specified above TestBench test setup.
LoadTestDriver stores a recorded scala script by default in the System's tmp-folder, but you can specify used output folder like in above example. Make sure that you have a write access to that folder.
**Note: Make sure you only run one test one time for the given directory and test name, othervice the resulting script might be broken.**

### Configure your TestBench test to open the application to be tested with your ip address:
```Java
private void openTestUrl() {
	// opens URL http://your.local.ip.address:8080/ui
    getDriver().get(LoadTestDriver.getLocalIpAddressWithPortAndContextPath(8080,"ui"));
}
```
**Note: The local IP address API above might not always work! In that case you have to explicitly hard code your ip v4 address into the test**

### Start your application in production mode
For example: `mvn spring-boot:run -Pproduction`

### Run the test as a JUnit test
LoadTestDriver uses Gatling to record the load test with parameters given in Driver setup (see above), test is saved in the given destination (see above).

### Example V14 project using the add-on
https://github.com/johannest/v14loadtestdriverdemo
