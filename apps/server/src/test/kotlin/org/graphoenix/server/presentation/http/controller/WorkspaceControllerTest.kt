package org.graphoenix.server.presentation.http.controller

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import org.graphoenix.server.createOrg
import org.graphoenix.server.presentation.http.controller.dto.*
import org.hamcrest.CoreMatchers.*
import org.hamcrest.Matchers.greaterThanOrEqualTo
import org.junit.jupiter.api.Test

@QuarkusTest
class WorkspaceControllerTest {
  @Test
  fun `should return new workspace ID`() {
    // Given
    val newOrgId = createOrg()

    // When
    given()
      .header("Content-Type", "application/json")
      .body(
        CreateWorkspaceDto(
          orgId = newOrgId.id,
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

  @Test
  fun `should get all workspaces by their organization ID`() {
    // Given
    val newOrgId = createOrg()
    val newWorkspaceIds =
      listOf(
        createWorkspace(newOrgId.id, "my new workspace A"),
        createWorkspace(newOrgId.id, "my new workspace B"),
      )

    // When
    given()
      .`when`()
      .get("/private/organizations/${newOrgId.id}/workspaces")
      .then()
      .statusCode(200)
      .body("size()", greaterThanOrEqualTo(2))
      .body("findAll { it.id == '%s' }.name".format(newWorkspaceIds[0].id), hasItem("my new workspace A"))
      .body("findAll { it.id == '%s' }.name".format(newWorkspaceIds[1].id), hasItem("my new workspace B"))
  }

  @Test
  fun `should get a workspace by its ID and org ID`() {
    // Given
    val newOrgId = createOrg()
    val newWorkspaceId = createWorkspace(newOrgId.id)

    // When
    given()
      .`when`()
      .get("/private/organizations/${newOrgId.id}/workspaces/${newWorkspaceId.id}")
      .then()
      .statusCode(200)
      .body("id", `is`(newWorkspaceId.id))
      .body("name", `is`("my new workspace"))
  }

  private fun createWorkspace(
    orgId: String,
    workspaceName: String = "my new workspace",
  ): IdDto =
    given()
      .header("Content-Type", "application/json")
      .body(CreateWorkspaceDto(orgId, workspaceName))
      .`when`()
      .post("/private/create-workspace")
      .`as`(IdDto::class.java)
}
