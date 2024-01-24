
import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class Bakery_SubWindow extends Simulation {

	val httpProtocol = http
		.baseUrl("http://192.168.99.1:8080")
		.acceptHeader("*/*")
		.acceptEncodingHeader("gzip, deflate")
		.acceptLanguageHeader("en-US,en;q=0.9")
		.userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36")

	val headers_0 = Map(
		"Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3",
		"Proxy-Connection" -> "keep-alive",
		"Upgrade-Insecure-Requests" -> "1")

	val headers_1 = Map(
		"Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3",
		"Origin" -> "http://192.168.99.1:8080",
		"Proxy-Connection" -> "keep-alive",
		"Upgrade-Insecure-Requests" -> "1")

	val headers_2 = Map(
		"Origin" -> "http://192.168.99.1:8080",
		"Proxy-Connection" -> "keep-alive")

	val headers_3 = Map(
		"Content-Type" -> "application/json; charset=UTF-8",
		"Origin" -> "http://192.168.99.1:8080",
		"Proxy-Connection" -> "keep-alive")

	val headers_5 = Map(
		"Content-Type" -> "application/json; charset=UTF-8",
		"Proxy-Connection" -> "keep-alive")



	val scn = scenario("Bakery_SubWindow")
		.exec(http("request_0")
			.get("/")
			.headers(headers_0))
		.pause(507 milliseconds)
		.exec(http("request_1")
			.post("/login")
			.headers(headers_1)
			.formParam("username", "barista@vaadin.com")
			.formParam("password", "barista"))
		.exec(http("request_2")
			.post("/?v-1564664804253")
			.headers(headers_2)
			.formParam("v-browserDetails", "1")
			.formParam("theme", "apptheme")
			.formParam("v-appId", "ROOT-2521314")
			.formParam("v-sh", "1440")
			.formParam("v-sw", "2560")
			.formParam("v-cw", "1251")
			.formParam("v-ch", "1249")
			.formParam("v-curdate", "1564664804254")
			.formParam("v-tzo", "-180")
			.formParam("v-dstd", "60")
			.formParam("v-rtzo", "-120")
			.formParam("v-dston", "true")
			.formParam("v-tzid", "Europe/Helsinki")
			.formParam("v-vw", "1251")
			.formParam("v-vh", "0")
			.formParam("v-loc", "http://192.168.99.1:8080/")
			.formParam("v-wn", "ROOT-2521314-0.5836834938762756")
			.check(bodyBytes.is(RawFileBody("Bakery_SubWindow_0002_response.txt"))))
		.pause(283 milliseconds)
		.exec(http("request_3")
			.post("/vaadinServlet/UIDL/?v-uiId=0")
			.headers(headers_3)
			.body(RawFileBody("Bakery_SubWindow_0003_request.txt"))
			.check(bodyBytes.is(RawFileBody("Bakery_SubWindow_0003_response.txt"))))
		.pause(254 milliseconds)
		.exec(http("request_4")
			.post("/vaadinServlet/UIDL/?v-uiId=0")
			.headers(headers_3)
			.body(RawFileBody("Bakery_SubWindow_0004_request.txt"))
			.check(bodyBytes.is(RawFileBody("Bakery_SubWindow_0004_response.txt"))))
		.pause(189 milliseconds)
		.exec(http("request_5")
			.get("/vaadinServlet/PUSH?v-uiId=0&v-pushId=7f1437e1-590b-4a09-bb5f-56352c70f1af&X-Atmosphere-tracking-id=0&X-Atmosphere-Framework=2.3.2.vaadin1-javascript&X-Atmosphere-Transport=long-polling&X-Atmosphere-TrackMessageSize=true&Content-Type=application%2Fjson%3B%20charset%3DUTF-8&X-atmo-protocol=true&_=1564664805281")
			.headers(headers_5)
			.check(bodyBytes.is(RawFileBody("Bakery_SubWindow_0005_response.txt"))))
		.exec(http("request_6")
			.get("/vaadinServlet/PUSH?v-uiId=0&v-pushId=7f1437e1-590b-4a09-bb5f-56352c70f1af&X-Atmosphere-tracking-id=4f31a255-faad-4cdb-bcb4-acb7597ac143&X-Atmosphere-Framework=2.3.2.vaadin1-javascript&X-Atmosphere-Transport=long-polling&X-Atmosphere-TrackMessageSize=true&Content-Type=application%2Fjson%3B%20charset%3DUTF-8&X-atmo-protocol=true&_=1564664805292")
			.headers(headers_5)
			.check(bodyBytes.is(RawFileBody("Bakery_SubWindow_0006_response.txt"))))
		.pause(5)
		.exec(http("request_7")
			.post("/vaadinServlet/UIDL/?v-uiId=0")
			.headers(headers_3)
			.body(RawFileBody("Bakery_SubWindow_0007_request.txt"))
			.check(bodyBytes.is(RawFileBody("Bakery_SubWindow_0007_response.txt"))))

	setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}