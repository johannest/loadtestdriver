
import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class LoginTest extends Simulation {

	val httpProtocol = http
		.baseURL("http://192.168.99.1:8090")
		.acceptHeader("*/*")
		.acceptEncodingHeader("gzip, deflate")
		.acceptLanguageHeader("en-US,en;q=0.9")
		.userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36")

	val headers_0 = Map(
		"Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8",
		"Upgrade-Insecure-Requests" -> "1",
		"content-length" -> "0")

	val headers_1 = Map(
		"Origin" -> "http://192.168.99.1:8090",
		"content-length" -> "0")

	val headers_2 = Map(
		"Content-type" -> "application/json; charset=UTF-8",
		"Origin" -> "http://192.168.99.1:8090")

    val uri1 = "https://fonts.googleapis.com/css"

	val scn = scenario("LoginTest")
		.exec(http("request_0")
			.get("/")
			.headers(headers_0)
			.check(bodyBytes.is(RawFileBody("LoginTest_0000_response.txt"))))
		.exec(http("request_1")
			.get("/frontend-es6/vaadin-flow-bundle-c38d404bf42b690.cache.html")
			.headers(headers_1)
			.check(bodyBytes.is(RawFileBody("LoginTest_0001_response.txt"))))
		.pause(622 milliseconds)
		.exec(http("request_2")
			.post("/?v-r=uidl&v-uiId=0")
			.headers(headers_2)
			.body(RawFileBody("LoginTest_0002_request.txt"))
			.check(bodyBytes.is(RawFileBody("LoginTest_0002_response.txt"))))
		.exec(http("request_3")
			.post("/?v-r=uidl&v-uiId=0")
			.headers(headers_2)
			.body(RawFileBody("LoginTest_0003_request.txt"))
			.check(bodyBytes.is(RawFileBody("LoginTest_0003_response.txt"))))
		.pause(112 milliseconds)
		.exec(http("request_4")
			.post("/?v-r=uidl&v-uiId=0")
			.headers(headers_2)
			.body(RawFileBody("LoginTest_0004_request.txt"))
			.check(bodyBytes.is(RawFileBody("LoginTest_0004_response.txt"))))
		.exec(http("request_5")
			.post("/?v-r=uidl&v-uiId=0")
			.headers(headers_2)
			.body(RawFileBody("LoginTest_0005_request.txt"))
			.check(bodyBytes.is(RawFileBody("LoginTest_0005_response.txt"))))
		.exec(http("request_6")
			.post("/?v-r=uidl&v-uiId=0")
			.headers(headers_2)
			.body(RawFileBody("LoginTest_0006_request.txt"))
			.check(bodyBytes.is(RawFileBody("LoginTest_0006_response.txt"))))
		.exec(http("request_7")
			.post("/?v-r=uidl&v-uiId=0")
			.headers(headers_2)
			.body(RawFileBody("LoginTest_0007_request.txt"))
			.check(bodyBytes.is(RawFileBody("LoginTest_0007_response.txt"))))
		.exec(http("request_8")
			.post("/?v-r=uidl&v-uiId=0")
			.headers(headers_2)
			.body(RawFileBody("LoginTest_0008_request.txt"))
			.check(bodyBytes.is(RawFileBody("LoginTest_0008_response.txt"))))

	setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}