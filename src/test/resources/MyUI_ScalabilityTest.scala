
import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class MyUI_ScalabilityTest extends Simulation {

	val httpProtocol = http
		.baseURL("http://192.168.1.117:8080")
		.acceptHeader("*/*")
		.acceptEncodingHeader("gzip, deflate")
		.acceptLanguageHeader("en-US,*")
		.userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X) AppleWebKit/538.1 (KHTML, like Gecko) PhantomJS/2.0.0 Safari/538.1")

	val headers_0 = Map("Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")

	val headers_1 = Map("Origin" -> "http://192.168.1.117:8080")

	val headers_2 = Map(
		"Content-Type" -> "application/json; charset=UTF-8",
		"Origin" -> "http://192.168.1.117:8080")



	val scn = scenario("MyUI_ScalabilityTest")
		.exec(http("request_0")
			.get("/ui")
			.headers(headers_0)
			.check(bodyBytes.is(RawFileBody("MyUI_ScalabilityTest_0000_response.txt"))))
		.pause(119 milliseconds)
		.exec(http("request_1")
			.post("/ui?v-1508730420204")
			.headers(headers_1)
			.body(RawFileBody("MyUI_ScalabilityTest_0001_request.txt"))
			.check(bodyBytes.is(RawFileBody("MyUI_ScalabilityTest_0001_response.txt"))))
		.pause(638 milliseconds)
		.exec(http("request_2")
			.post("/UIDL/?v-uiId=0")
			.headers(headers_2)
			.body(RawFileBody("MyUI_ScalabilityTest_0002_request.txt"))
			.check(bodyBytes.is(RawFileBody("MyUI_ScalabilityTest_0002_response.txt"))))
		.pause(280 milliseconds)
		.exec(http("request_3")
			.post("/UIDL/?v-uiId=0")
			.headers(headers_2)
			.body(RawFileBody("MyUI_ScalabilityTest_0003_request.txt"))
			.check(bodyBytes.is(RawFileBody("MyUI_ScalabilityTest_0003_response.txt"))))
		.pause(961 milliseconds)
		.exec(http("request_4")
			.post("/UIDL/?v-uiId=0")
			.headers(headers_2)
			.body(RawFileBody("MyUI_ScalabilityTest_0004_request.txt"))
			.check(bodyBytes.is(RawFileBody("MyUI_ScalabilityTest_0004_response.txt"))))
		.pause(105 milliseconds)
		.exec(http("request_5")
			.post("/UIDL/?v-uiId=0")
			.headers(headers_2)
			.body(RawFileBody("MyUI_ScalabilityTest_0005_request.txt"))
			.check(bodyBytes.is(RawFileBody("MyUI_ScalabilityTest_0005_response.txt"))))
		.pause(155 milliseconds)
		.exec(http("request_6")
			.post("/UIDL/?v-uiId=0")
			.headers(headers_2)
			.body(RawFileBody("MyUI_ScalabilityTest_0006_request.txt"))
			.check(bodyBytes.is(RawFileBody("MyUI_ScalabilityTest_0006_response.txt"))))
		.pause(140 milliseconds)
		.exec(http("request_7")
			.post("/UIDL/?v-uiId=0")
			.headers(headers_2)
			.body(RawFileBody("MyUI_ScalabilityTest_0007_request.txt"))
			.check(bodyBytes.is(RawFileBody("MyUI_ScalabilityTest_0007_response.txt"))))
		.exec(http("request_8")
			.post("/UIDL/?v-uiId=0")
			.headers(headers_2)
			.body(RawFileBody("MyUI_ScalabilityTest_0008_request.txt"))
			.check(bodyBytes.is(RawFileBody("MyUI_ScalabilityTest_0008_response.txt"))))
		.exec(http("request_9")
			.post("/UIDL/?v-uiId=0")
			.headers(headers_2)
			.body(RawFileBody("MyUI_ScalabilityTest_0009_request.txt"))
			.check(bodyBytes.is(RawFileBody("MyUI_ScalabilityTest_0009_response.txt"))))
		.pause(1)
		.exec(http("request_10")
			.post("/UIDL/?v-uiId=0")
			.headers(headers_2)
			.body(RawFileBody("MyUI_ScalabilityTest_0010_request.txt"))
			.check(bodyBytes.is(RawFileBody("MyUI_ScalabilityTest_0010_response.txt"))))
		.pause(146 milliseconds)
		.exec(http("request_11")
			.post("/UIDL/?v-uiId=0")
			.headers(headers_2)
			.body(RawFileBody("MyUI_ScalabilityTest_0011_request.txt"))
			.check(bodyBytes.is(RawFileBody("MyUI_ScalabilityTest_0011_response.txt"))))

	setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}