package org.graphoenix.server.presentation.http.controller

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.Test

@QuarkusTest
class PingControllerTest {
  @Test
  fun testHelloEndpoint() {
    given()
      .`when`()
      .get("/ping")
      .then()
      .statusCode(200)
      .body(`is`(""))
  }
}
