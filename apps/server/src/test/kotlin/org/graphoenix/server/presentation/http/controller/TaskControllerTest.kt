package org.graphoenix.server.presentation.http.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import org.graphoenix.server.*
import org.hamcrest.CoreMatchers.hasItem
import org.hamcrest.Matchers.greaterThanOrEqualTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@QuarkusTest
class TaskControllerTest {
  private val objectMapper: ObjectMapper = jacksonObjectMapper().registerModule(JavaTimeModule())

  private lateinit var token: String
  private lateinit var workspaceId: String

  @BeforeEach
  fun setUp() {
    token = prepareWorkspaceAndAccessToken("TaskControllerTest")
    workspaceId = getWorkspaceId(token)
  }

  @Test
  fun `should get a page of tasks by run`() {
    // Given
    val dummyRun = prepareExistingArtifact(token, objectMapper, "linkIdLol")

    // When
    given()
      .`when`()
      .get("/private/runs/${dummyRun.linkId}/$workspaceId/tasks?pageIndex=0&pageSize=100")
      .then()
      .log()
      .body()
      .statusCode(200)
      .body("totalCount", greaterThanOrEqualTo(1))
      .body("items.findAll { it.taskId == '%s' }.cacheStatus".format(dummyRun.tasks.first().taskId), hasItem("cache-miss"))
  }
}
