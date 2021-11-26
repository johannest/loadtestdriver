
import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class vaadin_bookstore extends Simulation {

	val httpProtocol = http
		.baseUrl("https://vaadin-bookstore-example.demo.vaadin.com")
		.acceptHeader("*/*")
		.acceptEncodingHeader("gzip, deflate")
		.acceptLanguageHeader("en-US,en;q=0.5")
		.doNotTrackHeader("1")
		.userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:93.0) Gecko/20100101 Firefox/93.0")

	val headers_0 = Map(
		"Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8",
		"Pragma" -> "no-cache",
		"Sec-Fetch-Dest" -> "document",
		"Sec-Fetch-Mode" -> "navigate",
		"Sec-Fetch-Site" -> "none",
		"Sec-Fetch-User" -> "?1",
		"Upgrade-Insecure-Requests" -> "1")

	val headers_1 = Map(
		"Accept" -> "image/avif,image/webp,*/*",
		"Pragma" -> "no-cache",
		"Sec-Fetch-Dest" -> "image",
		"Sec-Fetch-Mode" -> "no-cors",
		"Sec-Fetch-Site" -> "same-origin")

	val headers_4 = Map(
		"Content-type" -> "application/json; charset=UTF-8",
		"Origin" -> "https://vaadin-bookstore-example.demo.vaadin.com",
		"Pragma" -> "no-cache",
		"Sec-Fetch-Dest" -> "empty",
		"Sec-Fetch-Mode" -> "cors",
		"Sec-Fetch-Site" -> "same-origin")

	val headers_5 = Map(
		"Accept" -> "image/avif,image/webp,*/*",
		"Pragma" -> "no-cache",
		"Sec-Fetch-Dest" -> "image",
		"Sec-Fetch-Mode" -> "no-cors",
		"Sec-Fetch-Site" -> "same-site")

    val uri1 = "https://vaadin.com/images/vaadin-logo.svg"

	val scn = scenario("vaadin_bookstore")
		.exec(http("request_0")
			.get("/")
			.headers(headers_0)
			.check(bodyBytes.is(RawFileBody("vaadin_bookstore_0000_response.txt"))))
		.pause(362 milliseconds)
		.exec(http("request_1")
			.get("/icons/icon-180x180.png?-881012032")
			.headers(headers_1)
			.check(bodyBytes.is(RawFileBody("vaadin_bookstore_0001_response.txt"))))
		.exec(http("request_2")
			.get("/icons/icon-32x32.png?1051934160")
			.headers(headers_1)
			.check(bodyBytes.is(RawFileBody("vaadin_bookstore_0002_response.txt"))))
		.pause(166 milliseconds)
		.exec(http("request_3")
			.get("/frontend/external-link-alt-solid-white.svg")
			.headers(headers_1)
			.check(bodyBytes.is(RawFileBody("vaadin_bookstore_0003_response.txt"))))
		.exec(http("request_4")
			.post("/?v-r=uidl&v-uiId=0")
			.headers(headers_4)
			.body(RawFileBody("vaadin_bookstore_0004_request.txt"))
			.check(bodyBytes.is(RawFileBody("vaadin_bookstore_0004_response.txt"))))
		.exec(http("request_5")
			.get(uri1 + "")
			.headers(headers_5)
			.check(bodyBytes.is(RawFileBody("vaadin_bookstore_0005_response.txt"))))
		.pause(16)
		.exec(http("request_6")
			.post("/?v-r=uidl&v-uiId=0")
			.headers(headers_4)
			.body(RawFileBody("vaadin_bookstore_0006_request.txt"))
			.check(bodyBytes.is(RawFileBody("vaadin_bookstore_0006_response.txt"))))
		.pause(238 milliseconds)
		.exec(http("request_7")
			.post("/?v-r=uidl&v-uiId=0")
			.headers(headers_4)
			.body(RawFileBody("vaadin_bookstore_0007_request.txt"))
			.check(bodyBytes.is(RawFileBody("vaadin_bookstore_0007_response.txt"))))
		.pause(182 milliseconds)
		.exec(http("request_8")
			.post("/?v-r=uidl&v-uiId=0")
			.headers(headers_4)
			.body(RawFileBody("vaadin_bookstore_0008_request.txt"))
			.check(bodyBytes.is(RawFileBody("vaadin_bookstore_0008_response.txt"))))
		.pause(18)
		.exec(http("request_9")
			.post("/?v-r=uidl&v-uiId=0")
			.headers(headers_4)
			.body(RawFileBody("vaadin_bookstore_0009_request.txt"))
			.check(bodyBytes.is(RawFileBody("vaadin_bookstore_0009_response.txt"))))
		.pause(172 milliseconds)
		.exec(http("request_10")
			.post("/?v-r=uidl&v-uiId=0")
			.headers(headers_4)
			.body(RawFileBody("vaadin_bookstore_0010_request.txt"))
			.check(bodyBytes.is(RawFileBody("vaadin_bookstore_0010_response.txt"))))
		.exec(http("request_11")
			.post("/?v-r=uidl&v-uiId=0")
			.headers(headers_4)
			.body(RawFileBody("vaadin_bookstore_0011_request.txt"))
			.check(bodyBytes.is(RawFileBody("vaadin_bookstore_0011_response.txt"))))
		.pause(4)
		.exec(http("request_12")
			.post("/?v-r=uidl&v-uiId=0")
			.headers(headers_4)
			.body(RawFileBody("vaadin_bookstore_0012_request.txt"))
			.check(bodyBytes.is(RawFileBody("vaadin_bookstore_0012_response.txt"))))
		.pause(2)
		.exec(http("request_13")
			.post("/?v-r=uidl&v-uiId=0")
			.headers(headers_4)
			.body(RawFileBody("vaadin_bookstore_0013_request.txt"))
			.check(bodyBytes.is(RawFileBody("vaadin_bookstore_0013_response.txt"))))
		.exec(http("request_14")
			.post("/?v-r=uidl&v-uiId=0")
			.headers(headers_4)
			.body(RawFileBody("vaadin_bookstore_0014_request.txt"))
			.check(bodyBytes.is(RawFileBody("vaadin_bookstore_0014_response.txt"))))
		.pause(4)
		.exec(http("request_15")
			.post("/?v-r=uidl&v-uiId=0")
			.headers(headers_4)
			.body(RawFileBody("vaadin_bookstore_0015_request.txt"))
			.check(bodyBytes.is(RawFileBody("vaadin_bookstore_0015_response.txt"))))

	setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}