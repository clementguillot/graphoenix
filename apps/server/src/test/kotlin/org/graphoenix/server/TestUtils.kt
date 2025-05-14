package org.graphoenix.server

import com.fasterxml.jackson.databind.ObjectMapper
import io.restassured.RestAssured
import kotlinx.coroutines.*
import org.graphoenix.server.presentation.http.controller.dto.CreateOrgAndWorkspaceDto
import org.graphoenix.server.presentation.http.controller.dto.InitWorkspaceDto
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPOutputStream

fun prepareWorkspaceAndAccessToken(): String {
  val response =
    RestAssured
      .given()
      .header("Content-Type", "application/json")
      .body(
        CreateOrgAndWorkspaceDto(
          workspaceName = "test-workspace",
          installationSource = "junit",
          nxInitDate = null,
        ),
      ).post("/create-org-and-workspace")
      .`as`(InitWorkspaceDto::class.java)

  return response.token
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
