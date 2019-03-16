import io.gatling.core.body.ElFileBody
import io.gatling.core.body.ElFileBody
import io.gatling.core.body.ElFileBody
import io.gatling.core.body.ElFileBody
import io.gatling.core.body.ElFileBody
import io.gatling.core.body.ElFileBody

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

	val initSyncAndClientIds = exec((session) => {
		session.setAll(
			"syncId" -> 0,
			"clientId" -> 0
		)
	})


	val syncIdExtract = regex("""syncId": ([0-9]*),""").saveAs("syncId")
	val clientIdExtract = regex("""clientId": ([0-9]*),""").saveAs("clientId")
	val xsrfTokenExtract = regex("""Vaadin-Security-Key\\":\\"([^\\]+)""").saveAs("seckey")


	val extract_43_Id = regex("""[,\{]"([0-9]*)":\{[^\}]{0,250}"id":"pform\-pname""").saveAs("_43_Id")
	val extract_60_Id = regex("""[,\{]"([0-9]*)":\{[^\}]{0,250}"id":"pform\-save""").saveAs("_60_Id")
	val extract_30_Id = regex("""[,\{]"([0-9]*)":\{[^\}]{0,250}"id":"new\-product\-bttn""").saveAs("_30_Id")
	val extract_28_Id = regex("""[,\{]"([0-9]*)":\{[^\}]{0,250}"id":"crud\-filterfield""").saveAs("_28_Id")


	val initSyncAndClientIds = exec((session) => {
		session.setAll(
			"syncId" -> 0,
			"clientId" -> 0
		)
	})


	val syncIdExtract = regex("""syncId": ([0-9]*),""").saveAs("syncId")
	val clientIdExtract = regex("""clientId": ([0-9]*),""").saveAs("clientId")
	val xsrfTokenExtract = regex("""Vaadin-Security-Key\\":\\"([^\\]+)""").saveAs("seckey")




	val initSyncAndClientIds = exec((session) => {
		session.setAll(
			"syncId" -> 0,
			"clientId" -> 0
		)
	})


	val syncIdExtract = regex("""syncId": ([0-9]*),""").saveAs("syncId")
	val clientIdExtract = regex("""clientId": ([0-9]*),""").saveAs("clientId")
	val xsrfTokenExtract = regex("""Vaadin-Security-Key\\":\\"([^\\]+)""").saveAs("seckey")




	val initSyncAndClientIds = exec((session) => {
		session.setAll(
			"syncId" -> 0,
			"clientId" -> 0
		)
	})


	val syncIdExtract = regex("""syncId": ([0-9]*),""").saveAs("syncId")
	val clientIdExtract = regex("""clientId": ([0-9]*),""").saveAs("clientId")
	val xsrfTokenExtract = regex("""Vaadin-Security-Key\\":\\"([^\\]+)""").saveAs("seckey")




	val initSyncAndClientIds = exec((session) => {
		session.setAll(
			"syncId" -> 0,
			"clientId" -> 0
		)
	})


	val syncIdExtract = regex("""syncId": ([0-9]*),""").saveAs("syncId")
	val clientIdExtract = regex("""clientId": ([0-9]*),""").saveAs("clientId")
	val xsrfTokenExtract = regex("""Vaadin-Security-Key\\":\\"([^\\]+)""").saveAs("seckey")




	val initSyncAndClientIds = exec((session) => {
		session.setAll(
			"syncId" -> 0,
			"clientId" -> 0
		)
	})


	val syncIdExtract = regex("""syncId": ([0-9]*),""").saveAs("syncId")
	val clientIdExtract = regex("""clientId": ([0-9]*),""").saveAs("clientId")
	val xsrfTokenExtract = regex("""Vaadin-Security-Key\\":\\"([^\\]+)""").saveAs("seckey")




	val scn = scenario("MyUI_ScalabilityTest")
		.exec(initSyncAndClientIds)
		.pause(1, 5)
		.exec(initSyncAndClientIds)
		.pause(1, 5)
		.exec(initSyncAndClientIds)
		.pause(1, 5)
		.exec(initSyncAndClientIds)
		.pause(1, 5)
		.exec(initSyncAndClientIds)
		.pause(1, 5)
		.exec(initSyncAndClientIds)
		.pause(1, 5)
		.exec(http("request_0")
			.get("/ui")
			.headers(headers_0)
			)
		.pause(1, 5)
		.pause(1, 5)
		.pause(1, 5)
		.pause(1, 5)
		.pause(1, 5)
		.pause(1, 5)
		.exec(http("request_1")
			.post("/ui?v-1548145126755")
			.check(xsrfTokenExtract)
			.formParam("v-cw", "400")
			.check(xsrfTokenExtract)
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
			)
		.pause(889 milliseconds)
		.pause(1, 5)
		.pause(1, 5)
		.pause(1, 5)
		.pause(1, 5)
		.pause(1, 5)
		.pause(1, 5)
		.exec(http("request_2")
			.post("/UIDL/?v-uiId=0")
			.headers(headers_2)
			.check(syncIdExtract).check(clientIdExtract)
			.check(syncIdExtract).check(clientIdExtract)
			.check(syncIdExtract).check(clientIdExtract)
			.check(syncIdExtract).check(clientIdExtract)
			.check(syncIdExtract).check(clientIdExtract)
			.check(syncIdExtract).check(clientIdExtract)
			.check(extract_28_Id)
			.check(extract_30_Id)
			.check(extract_60_Id)
			.check(extract_43_Id)
			.body(ElFileBody("MyUI_ScalabilityTest_0002_request.txt"))
			)
		.pause(1, 5)
		.pause(1, 5)
		.pause(1, 5)
		.pause(1, 5)
		.pause(1, 5)
		.pause(1, 5)
		.exec(http("request_3")
			.get("/PUSH?v-uiId=0&v-csrfToken=f078d8ee-9df5-4bba-a63a-e93b26a8206a&X-Atmosphere-tracking-id=0&X-Atmosphere-Framework=2.2.13.vaadin5-javascript&X-Atmosphere-Transport=long-polling&X-Atmosphere-TrackMessageSize=true&Content-Type=application%2Fjson%3B%20charset%3DUTF-8&X-atmo-protocol=true&_=1548145127693")
			)
		.pause(804 milliseconds)
		.pause(1, 5)
		.pause(1, 5)
		.pause(1, 5)
		.pause(1, 5)
		.pause(1, 5)
		.pause(1, 5)
		.exec(http("request_4")
			.post("/UIDL/?v-uiId=0")
			.headers(headers_2)
			.check(syncIdExtract).check(clientIdExtract)
			.check(syncIdExtract).check(clientIdExtract)
			.check(syncIdExtract).check(clientIdExtract)
			.check(syncIdExtract).check(clientIdExtract)
			.check(syncIdExtract).check(clientIdExtract)
			.check(syncIdExtract).check(clientIdExtract)
			.body(ElFileBody("MyUI_ScalabilityTest_0004_request.txt"))
			)
		.pause(165 milliseconds)
		.pause(1, 5)
		.pause(1, 5)
		.pause(1, 5)
		.pause(1, 5)
		.pause(1, 5)
		.pause(1, 5)
		.exec(http("request_5")
			.post("/UIDL/?v-uiId=0")
			.headers(headers_2)
			.check(syncIdExtract).check(clientIdExtract)
			.check(syncIdExtract).check(clientIdExtract)
			.check(syncIdExtract).check(clientIdExtract)
			.check(syncIdExtract).check(clientIdExtract)
			.check(syncIdExtract).check(clientIdExtract)
			.check(syncIdExtract).check(clientIdExtract)
			.body(ElFileBody("MyUI_ScalabilityTest_0005_request.txt"))
			)
		.pause(246 milliseconds)
		.pause(1, 5)
		.pause(1, 5)
		.pause(1, 5)
		.pause(1, 5)
		.pause(1, 5)
		.pause(1, 5)
		.exec(http("request_6")
			.post("/UIDL/?v-uiId=0")
			.headers(headers_2)
			.check(syncIdExtract).check(clientIdExtract)
			.check(syncIdExtract).check(clientIdExtract)
			.check(syncIdExtract).check(clientIdExtract)
			.check(syncIdExtract).check(clientIdExtract)
			.check(syncIdExtract).check(clientIdExtract)
			.check(syncIdExtract).check(clientIdExtract)
			.body(ElFileBody("MyUI_ScalabilityTest_0006_request.txt"))
			)
		.pause(161 milliseconds)
		.pause(1, 5)
		.pause(1, 5)
		.pause(1, 5)
		.pause(1, 5)
		.pause(1, 5)
		.pause(1, 5)
		.exec(http("request_7")
			.post("/UIDL/?v-uiId=0")
			.headers(headers_2)
			.check(syncIdExtract).check(clientIdExtract)
			.check(syncIdExtract).check(clientIdExtract)
			.check(syncIdExtract).check(clientIdExtract)
			.check(syncIdExtract).check(clientIdExtract)
			.check(syncIdExtract).check(clientIdExtract)
			.check(syncIdExtract).check(clientIdExtract)
			.body(ElFileBody("MyUI_ScalabilityTest_0007_request.txt"))
			)
		.pause(157 milliseconds)
		.pause(1, 5)
		.pause(1, 5)
		.pause(1, 5)
		.pause(1, 5)
		.pause(1, 5)
		.pause(1, 5)
		.exec(http("request_8")
			.post("/UIDL/?v-uiId=0")
			.headers(headers_2)
			.check(syncIdExtract).check(clientIdExtract)
			.check(syncIdExtract).check(clientIdExtract)
			.check(syncIdExtract).check(clientIdExtract)
			.check(syncIdExtract).check(clientIdExtract)
			.check(syncIdExtract).check(clientIdExtract)
			.check(syncIdExtract).check(clientIdExtract)
			.body(ElFileBody("MyUI_ScalabilityTest_0008_request.txt"))
			)
		.pause(1, 5)
		.tryMax(5) {
			pause(10)
		.pause(1, 5)
		.pause(1, 5)
		.pause(1, 5)
		.pause(1, 5)
		.pause(1, 5)
				.exec(http("poll")
					.post("/UIDL/?v-uiId=0")
					.headers(headers_2)
			.check(syncIdExtract).check(clientIdExtract)
			.check(syncIdExtract).check(clientIdExtract)
			.check(syncIdExtract).check(clientIdExtract)
			.check(syncIdExtract).check(clientIdExtract)
			.check(syncIdExtract).check(clientIdExtract)
			.check(syncIdExtract).check(clientIdExtract)
					.body(StringBody("""{"csrfToken":"${seckey}","rpc":[["0","com.vaadin.shared.ui.ui.UIServerRpc","poll",[]]],"syncId":${syncId},"clientId":${clientId}}""")).asJson
					.check(regex("""updated"""))
				)
			}

	setUp(scn.inject(rampUsers(10) over (5 seconds))).protocols(httpProtocol)
}
