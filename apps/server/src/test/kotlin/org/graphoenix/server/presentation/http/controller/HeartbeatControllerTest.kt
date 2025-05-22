package org.graphoenix.server.presentation.http.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import org.graphoenix.server.prepareWorkspaceAndAccessToken
import org.graphoenix.server.presentation.http.controller.dto.HeartbeatDto
import org.graphoenix.server.serializeAndCompress
import org.junit.jupiter.api.Test

@QuarkusTest
class HeartbeatControllerTest {
  @Test
  fun `should return a OK code on heartbeat`() {
    val token = prepareWorkspaceAndAccessToken()

    given()
      .header("authorization", token)
      .header("Content-Type", "application/json")
      .body(
        HeartbeatDto(
          ciExecutionId = "test",
          runGroup = "junit",
          logs = null,
        ),
      ).`when`()
      .post("/heartbeat")
      .then()
      .statusCode(200)
  }

  @Test
  fun `should return a OK code when receiving heartbeat logs`() {
    val token = prepareWorkspaceAndAccessToken()

    given()
      .header("authorization", token)
      .header("Content-Type", "application/octet-stream")
      .body(
        serializeAndCompress(
          HeartbeatDto(
            ciExecutionId = "test",
            runGroup = "junit",
            logs = "test logs content",
          ),
          jacksonObjectMapper(),
        ),
      ).`when`()
      .post("/heartbeat/logs")
      .then()
      .statusCode(200)
  }
}
