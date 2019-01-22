
import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class MyUI_ScalabilityTest extends Simulation {

	val httpProtocol = http
		.baseUrl("http://192.168.99.1:8080")
		.acceptHeader("*/*")
		.acceptEncodingHeader("gzip, deflate")
		.acceptLanguageHeader("en-FI,*")
		.userAgentHeader("Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/538.1 (KHTML, like Gecko) PhantomJS/2.1.1 Safari/538.1")

	val headers_0 = Map("Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")

	val headers_1 = Map("Origin" -> "http://192.168.99.1:8080")

	val headers_2 = Map(
		"Content-Type" -> "application/json; charset=UTF-8",
		"Origin" -> "http://192.168.99.1:8080")

    val uri1 = "https://www.google.com:443"

	val scn = scenario("MyUI_ScalabilityTest")
		.exec(http("request_0")
			.get("/ui")
			.headers(headers_0)
			.check(bodyBytes.is(RawFileBody("MyUI_ScalabilityTest_0000_response.txt"))))
		.exec(http("request_1")
			.post("/ui?v-1548145126755")
			.headers(headers_1)
			.formParam("v-browserDetails", "1")
			.formParam("theme", "mytheme")
			.formParam("v-appId", "ROOT-2521314")
			.formParam("v-sh", "2160")
			.formParam("v-sw", "3840")
			.formParam("v-cw", "400")
			.formParam("v-ch", "300")
			.formParam("v-curdate", "1548145126755")
			.formParam("v-tzo", "-120")
			.formParam("v-dstd", "60")
			.formParam("v-rtzo", "-120")
			.formParam("v-dston", "false")
			.formParam("v-vw", "400")
			.formParam("v-vh", "0")
			.formParam("v-loc", "http://192.168.99.1:8080/ui")
			.formParam("v-wn", "ROOT-2521314-0.441602106904611")
			.formParam("v-td", "1")
			.check(bodyBytes.is(RawFileBody("MyUI_ScalabilityTest_0001_response.txt"))))
		.pause(889 milliseconds)
		.exec(http("request_2")
			.post("/UIDL/?v-uiId=0")
			.headers(headers_2)
			.body(RawFileBody("MyUI_ScalabilityTest_0002_request.txt"))
			.check(bodyBytes.is(RawFileBody("MyUI_ScalabilityTest_0002_response.txt"))))
		.exec(http("request_3")
			.get("/PUSH?v-uiId=0&v-csrfToken=f078d8ee-9df5-4bba-a63a-e93b26a8206a&X-Atmosphere-tracking-id=0&X-Atmosphere-Framework=2.2.13.vaadin5-javascript&X-Atmosphere-Transport=long-polling&X-Atmosphere-TrackMessageSize=true&Content-Type=application%2Fjson%3B%20charset%3DUTF-8&X-atmo-protocol=true&_=1548145127693")
			.check(bodyBytes.is(RawFileBody("MyUI_ScalabilityTest_0003_response.txt"))))
		.pause(804 milliseconds)
		.exec(http("request_4")
			.post("/UIDL/?v-uiId=0")
			.headers(headers_2)
			.body(RawFileBody("MyUI_ScalabilityTest_0004_request.txt"))
			.check(bodyBytes.is(RawFileBody("MyUI_ScalabilityTest_0004_response.txt"))))
		.pause(165 milliseconds)
		.exec(http("request_5")
			.post("/UIDL/?v-uiId=0")
			.headers(headers_2)
			.body(RawFileBody("MyUI_ScalabilityTest_0005_request.txt"))
			.check(bodyBytes.is(RawFileBody("MyUI_ScalabilityTest_0005_response.txt"))))
		.pause(246 milliseconds)
		.exec(http("request_6")
			.post("/UIDL/?v-uiId=0")
			.headers(headers_2)
			.body(RawFileBody("MyUI_ScalabilityTest_0006_request.txt"))
			.check(bodyBytes.is(RawFileBody("MyUI_ScalabilityTest_0006_response.txt"))))
		.pause(161 milliseconds)
		.exec(http("request_7")
			.post("/UIDL/?v-uiId=0")
			.headers(headers_2)
			.body(RawFileBody("MyUI_ScalabilityTest_0007_request.txt"))
			.check(bodyBytes.is(RawFileBody("MyUI_ScalabilityTest_0007_response.txt"))))
		.pause(157 milliseconds)
		.exec(http("request_8")
			.post("/UIDL/?v-uiId=0")
			.headers(headers_2)
			.body(RawFileBody("MyUI_ScalabilityTest_0008_request.txt"))
			.check(bodyBytes.is(RawFileBody("MyUI_ScalabilityTest_0008_response.txt"))))
		.exec(http("request_9")
			.get(uri1 + "/search?q=INJECT_TRYMAX_LOOP.5.10.updated")
			.headers(headers_0)
			.check(bodyBytes.is(RawFileBody("MyUI_ScalabilityTest_0009_response.txt"))))

	setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}