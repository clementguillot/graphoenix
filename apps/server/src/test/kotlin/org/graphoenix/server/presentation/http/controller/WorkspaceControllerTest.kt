package org.graphoenix.server.presentation.http.controller

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import jakarta.inject.Inject
import kotlinx.coroutines.test.runTest
import org.graphoenix.server.domain.workspace.gateway.OrganizationRepository
import org.graphoenix.server.presentation.http.controller.dto.CreateOrgAndWorkspaceDto
import org.graphoenix.server.presentation.http.controller.dto.CreateWorkspaceDto
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.jupiter.api.Test

@QuarkusTest
class WorkspaceControllerTest {
  @Inject
  lateinit var orgRepository: OrganizationRepository

  @Test
  fun `should return new workspace ID`() =
    runTest {
      val newOrg = orgRepository.create("dummy org")

      given()
        .header("Content-Type", "application/json")
        .body(
          CreateWorkspaceDto(
            orgId = newOrg.id.value,
            name = "new workspace",
          ),
        ).`when`()
        .post("/private/create-workspace")
        .then()
        .statusCode(200)
        .body("id", `is`(notNullValue()))
    }

  @Test
  fun `should initialize a new workspace and return an access token`() {
    given()
      .header("Content-Type", "application/json")
      .body(
        CreateOrgAndWorkspaceDto(
          workspaceName = "test-workspace",
          installationSource = "junit",
          nxInitDate = null,
        ),
      ).`when`()
      .post("/create-org-and-workspace")
      .then()
      .statusCode(200)
      .body("token", `is`(notNullValue()), "url", `is`(notNullValue()))
  }
}
