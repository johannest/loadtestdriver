
import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class Bakery_AddOrder extends Simulation {

	val httpProtocol = http
		.baseUrl("http://192.168.99.1:8090")
		.acceptHeader("*/*")
		.acceptEncodingHeader("gzip, deflate")
		.acceptLanguageHeader("en-US,en;q=0.9")
		.userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36")

	val headers_0 = Map(
		"Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8",
		"Proxy-Connection" -> "keep-alive",
		"Upgrade-Insecure-Requests" -> "1")

	val headers_1 = Map(
		"Origin" -> "http://192.168.99.1:8090",
		"Proxy-Connection" -> "keep-alive")

	val headers_2 = Map(
		"Content-type" -> "application/json; charset=UTF-8",
		"Origin" -> "http://192.168.99.1:8090",
		"Proxy-Connection" -> "keep-alive")

	val headers_3 = Map("Proxy-Connection" -> "keep-alive")

	val headers_4 = Map(
		"Accept" -> "image/webp,image/apng,image/*,*/*;q=0.8",
		"Proxy-Connection" -> "keep-alive")

	val headers_6 = Map(
		"Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8",
		"Origin" -> "http://192.168.99.1:8090",
		"Proxy-Connection" -> "keep-alive",
		"Upgrade-Insecure-Requests" -> "1")



	val scn = scenario("Bakery_AddOrder")
		.exec(http("request_0")
			.get("/login")
			.headers(headers_0)
			.check(bodyBytes.is(RawFileBody("Bakery_AddOrder_0000_response.txt"))))
		.exec(http("request_1")
			.get("/frontend-es6/vaadin-flow-bundle-1d10af7dcc10c1e.cache.html")
			.headers(headers_1)
			.check(bodyBytes.is(RawFileBody("Bakery_AddOrder_0001_response.txt"))))
		.pause(400 milliseconds)
		.exec(http("request_2")
			.post("/?v-r=uidl&v-uiId=0")
			.headers(headers_2)
			.body(RawFileBody("Bakery_AddOrder_0002_request.txt"))
			.check(bodyBytes.is(RawFileBody("Bakery_AddOrder_0002_response.txt"))))
		.exec(http("request_3")
			.get("/offline-page.html")
			.headers(headers_3)
			.check(bodyBytes.is(RawFileBody("Bakery_AddOrder_0003_response.txt"))))
		.exec(http("request_4")
			.get("/icons/icon-32x32.png?868055559")
			.headers(headers_4)
			.check(bodyBytes.is(RawFileBody("Bakery_AddOrder_0004_response.txt"))))
		.pause(285 milliseconds)
		.exec(http("request_5")
			.post("/?v-r=uidl&v-uiId=0")
			.headers(headers_2)
			.body(RawFileBody("Bakery_AddOrder_0005_request.txt"))
			.check(bodyBytes.is(RawFileBody("Bakery_AddOrder_0005_response.txt"))))
		.exec(http("request_6")
			.post("/login")
			.headers(headers_6)
			.formParam("username", "admin@vaadin.com")
			.formParam("password", "admin"))
		.exec(http("request_7")
			.get("/frontend-es6/main-fragment-f58c2e702846b3f.cache.html")
			.headers(headers_1)
			.check(bodyBytes.is(RawFileBody("Bakery_AddOrder_0007_response.txt"))))
		.exec(http("request_8")
			.get("/frontend-es6/grid-fragment-bc976e1a5254b3e.cache.html")
			.headers(headers_1)
			.check(bodyBytes.is(RawFileBody("Bakery_AddOrder_0008_response.txt"))))
		.pause(194 milliseconds)
		.exec(http("request_9")
			.post("/?v-r=uidl&v-uiId=1")
			.headers(headers_2)
			.body(RawFileBody("Bakery_AddOrder_0009_request.txt"))
			.check(bodyBytes.is(RawFileBody("Bakery_AddOrder_0009_response.txt"))))
		.exec(http("request_10")
			.post("/?v-r=uidl&v-uiId=1")
			.headers(headers_2)
			.body(RawFileBody("Bakery_AddOrder_0010_request.txt"))
			.check(bodyBytes.is(RawFileBody("Bakery_AddOrder_0010_response.txt"))))
		.pause(208 milliseconds)
		.exec(http("request_11")
			.post("/?v-r=uidl&v-uiId=1")
			.headers(headers_2)
			.body(RawFileBody("Bakery_AddOrder_0011_request.txt"))
			.check(bodyBytes.is(RawFileBody("Bakery_AddOrder_0011_response.txt"))))
		.exec(http("request_12")
			.post("/?v-r=uidl&v-uiId=1")
			.headers(headers_2)
			.body(RawFileBody("Bakery_AddOrder_0012_request.txt"))
			.check(bodyBytes.is(RawFileBody("Bakery_AddOrder_0012_response.txt"))))
		.exec(http("request_13")
			.post("/?v-r=uidl&v-uiId=1")
			.headers(headers_2)
			.body(RawFileBody("Bakery_AddOrder_0013_request.txt"))
			.check(bodyBytes.is(RawFileBody("Bakery_AddOrder_0013_response.txt"))))
		.pause(151 milliseconds)
		.exec(http("request_14")
			.post("/?v-r=uidl&v-uiId=1")
			.headers(headers_2)
			.body(RawFileBody("Bakery_AddOrder_0014_request.txt"))
			.check(bodyBytes.is(RawFileBody("Bakery_AddOrder_0014_response.txt"))))
		.pause(122 milliseconds)
		.exec(http("request_15")
			.post("/?v-r=uidl&v-uiId=1")
			.headers(headers_2)
			.body(RawFileBody("Bakery_AddOrder_0015_request.txt"))
			.check(bodyBytes.is(RawFileBody("Bakery_AddOrder_0015_response.txt"))))
		.exec(http("request_16")
			.post("/?v-r=uidl&v-uiId=1")
			.headers(headers_2)
			.body(RawFileBody("Bakery_AddOrder_0016_request.txt"))
			.check(bodyBytes.is(RawFileBody("Bakery_AddOrder_0016_response.txt"))))
		.exec(http("request_17")
			.post("/?v-r=uidl&v-uiId=1")
			.headers(headers_2)
			.body(RawFileBody("Bakery_AddOrder_0017_request.txt"))
			.check(bodyBytes.is(RawFileBody("Bakery_AddOrder_0017_response.txt"))))
		.exec(http("request_18")
			.post("/?v-r=uidl&v-uiId=1")
			.headers(headers_2)
			.body(RawFileBody("Bakery_AddOrder_0018_request.txt"))
			.check(bodyBytes.is(RawFileBody("Bakery_AddOrder_0018_response.txt"))))

	setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}