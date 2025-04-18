package org.graphoenix.server

import com.fasterxml.jackson.databind.ObjectMapper
import io.restassured.RestAssured.given
import kotlinx.coroutines.*
import org.graphoenix.server.presentation.http.controller.dto.*
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPOutputStream

fun createOrg(name: String = "my new org"): IdDto =
  given()
    .header("Content-Type", "application/json")
    .body(CreateOrganizationDto(name))
    .`when`()
    .post("/private/create-org")
    .`as`(IdDto::class.java)

fun prepareWorkspaceAndAccessToken(workspaceName: String = "test-workspace"): String =
  given()
    .header("Content-Type", "application/json")
    .body(
      CreateOrgAndWorkspaceDto(
        workspaceName = workspaceName,
        installationSource = "junit",
        nxInitDate = null,
      ),
    ).post("/create-org-and-workspace")
    .`as`(InitWorkspaceDto::class.java)
    .token

fun getWorkspaceId(workspaceName: String): String =
  given()
    .`when`()
    .get("/private/organizations")
    .`as`(Array<OrganizationDto>::class.java)
    .let { orgs ->
      orgs.find { it.name == workspaceName }.let { org ->
        given()
          .`when`()
          .get("/private/organizations/${org?.id}/workspaces")
          .`as`(Array<WorkspaceDto>::class.java)
          .first()
          .id
      }
    }

suspend fun serializeAndCompress(
  dto: Any,
  objectMapper: ObjectMapper,
): ByteArray =
  coroutineScope {
    withContext(Dispatchers.IO) {
      val json = objectMapper.writeValueAsString(dto)
      val outputStream = ByteArrayOutputStream()
      GZIPOutputStream(outputStream).bufferedWriter().use { it.write(json) }
      outputStream.toByteArray()
    }
  }
