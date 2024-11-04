package org.nxcloudce.server

import com.fasterxml.jackson.databind.ObjectMapper
import io.restassured.RestAssured
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.nxcloudce.server.presentation.dto.CreateOrgAndWorkspaceDto
import org.nxcloudce.server.presentation.dto.InitWorkspaceDto
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
  dispatcher: CoroutineDispatcher,
  objectMapper: ObjectMapper,
): ByteArray =
  coroutineScope {
    withContext(dispatcher) {
      val json = objectMapper.writeValueAsString(dto)
      val outputStream = ByteArrayOutputStream()
      GZIPOutputStream(outputStream).bufferedWriter().use { it.write(json) }
      outputStream.toByteArray()
    }
  }
