package org.graphoenix.server.presentation.http.controller

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import org.graphoenix.server.createOrg
import org.graphoenix.server.presentation.http.controller.dto.CreateOrganizationDto
import org.hamcrest.CoreMatchers.*
import org.hamcrest.Matchers.greaterThanOrEqualTo
import org.junit.jupiter.api.Test

@QuarkusTest
class OrganizationControllerTest {
  @Test
  fun `should return new object ID`() {
    given()
      .header("Content-Type", "application/json")
      .body(CreateOrganizationDto("my new org"))
      .`when`()
      .post("/private/create-org")
      .then()
      .statusCode(200)
      .body("id", `is`(notNullValue()))
  }

  @Test
  fun `should get all organizations`() {
    // Given
    val newOrgIds = listOf(createOrg("my new org A"), createOrg("my new org B"))

    // When
    given()
      .`when`()
      .get("/private/organizations")
      .then()
      .statusCode(200)
      .body("size()", greaterThanOrEqualTo(2))
      .body("findAll { it.id == '%s' }.name".format(newOrgIds[0].id), hasItem("my new org A"))
      .body("findAll { it.id == '%s' }.name".format(newOrgIds[1].id), hasItem("my new org B"))
  }

  @Test
  fun `should get an organization by its ID`() {
    // Given
    val newOrgId = createOrg()

    // When
    given()
      .`when`()
      .get("/private/organizations/${newOrgId.id}")
      .then()
      .statusCode(200)
      .body("id", `is`(newOrgId.id))
      .body("name", `is`("my new org"))
  }
}
