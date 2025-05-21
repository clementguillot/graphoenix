package org.graphoenix.server.presentation.http.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import org.graphoenix.server.*
import org.graphoenix.server.domain.run.valueobject.MachineInfo
import org.graphoenix.server.presentation.http.controller.dto.RunDto
import org.hamcrest.CoreMatchers.*
import org.hamcrest.Matchers.greaterThanOrEqualTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

@QuarkusTest
class RunControllerTest {
  private val objectMapper: ObjectMapper = jacksonObjectMapper().registerModule(JavaTimeModule())

  private lateinit var token: String
  private lateinit var workspaceId: String

  @BeforeEach
  fun setUp() {
    token = prepareWorkspaceAndAccessToken("RunControllerTest")
    workspaceId = getWorkspaceId(token)
  }

  @Test
  fun `should start a new run and return a list of URLs to access cached artifact`() {
    // Given
    prepareExistingArtifact(token, objectMapper)

    // When
    given()
      .header("authorization", token)
      .header("Content-Type", "application/json")
      .body(
        RunDto.Start(
          branch = null,
          runGroup = "run-group",
          ciExecutionId = null,
          ciExecutionEnv = null,
          distributedExecutionId = null,
          hashes = listOf("hash-new", "hash-existing"),
          machineInfo =
            MachineInfo(
              machineId = "junit",
              platform = "test",
              version = "1",
              cpuCores = 1,
            ),
          meta = mapOf("nxCloudVersion" to "123"),
          vcsContext = null,
          clientInstanceId = UUID.randomUUID(),
          clientInstanceSource = "CLOUD_RUNNER",
        ),
      ).`when`()
      .post("/v2/runs/start")
      .then()
      .log()
      .body()
      .statusCode(200)
      .body(
        "artifacts.size()",
        `is`(2),
        "artifacts.hash-existing.artifactUrls.get",
        `is`(notNullValue()),
        "artifacts.hash-existing.artifactUrls.put",
        `is`(notNullValue()),
        "artifacts.hash-new.artifactUrls.get",
        `is`(nullValue()),
        "artifacts.hash-new.artifactUrls.put",
        `is`(notNullValue()),
      )
  }

  @Test
  fun `should end a successful run`() {
    // When
    given()
      .header("authorization", token)
      .header("Content-Type", "application/octet-stream")
      .body(
        serializeAndCompress(
          buildEndRunDto(
            listOf(
              buildTaskDto("1"),
              buildTaskDto("2"),
            ),
            "test-link-id",
          ),
          objectMapper,
        ),
      ).`when`()
      .post("/runs/end")
      .then()
      .statusCode(200)
      .body(
        "runUrl",
        `is`("http://localtest/runs/test-link-id"),
        "status",
        `is`("success"),
      )
  }

  @Test
  fun `should end a failure run`() {
    // When
    given()
      .header("authorization", token)
      .header("Content-Type", "application/octet-stream")
      .body(
        serializeAndCompress(
          buildEndRunDto(
            listOf(
              buildTaskDto("1"),
              buildTaskDto("2", 1),
            ),
            "test-link-id",
          ),
          objectMapper,
        ),
      ).`when`()
      .post("/runs/end")
      .then()
      .statusCode(200)
      .body(
        "runUrl",
        `is`("http://localtest/runs/test-link-id"),
        "status",
        `is`("success"),
      )
  }

  @Test
  fun `should end run and generate its link ID`() {
    // When
    given()
      .header("authorization", token)
      .header("Content-Type", "application/octet-stream")
      .body(
        serializeAndCompress(
          buildEndRunDto(
            listOf(
              buildTaskDto("1"),
              buildTaskDto("2"),
            ),
            null,
          ),
          objectMapper,
        ),
      ).`when`()
      .post("/runs/end")
      .then()
      .statusCode(200)
      .body(
        "status",
        `is`("success"),
      )
  }

  @Test
  fun `should return a healthy response when workspace is authenticated`() {
    // Given
    val token = prepareWorkspaceAndAccessToken()

    // When
    given()
      .header("authorization", token)
      .`when`()
      .get("/runs/workspace-status")
      .then()
      .statusCode(200)
  }

  @Test
  fun `should get a page of runs by workspace ID`() {
    // Given
    val dummyRun = prepareExistingArtifact(token, objectMapper)

    // When
    given()
      .`when`()
      .get("/private/workspaces/$workspaceId/runs?pageIndex=0&pageSize=100")
      .then()
      .statusCode(200)
      .body("totalCount", greaterThanOrEqualTo(1))
      .body("items.find { it.command == '%s' }.status".format(dummyRun.run.command), `is`(0))
  }

  @Test
  fun `should get a run details`() {
    // Given
    val dummyRun = prepareExistingArtifact(token, objectMapper, "linkId")

    // When
    given()
      .`when`()
      .get("/private/runs/${dummyRun.linkId}")
      .then()
      .statusCode(200)
      .body("command", equalTo(dummyRun.run.command))
  }
}
