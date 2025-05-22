package org.graphoenix.server

import com.fasterxml.jackson.databind.ObjectMapper
import io.restassured.RestAssured.given
import org.graphoenix.server.domain.run.entity.Run
import org.graphoenix.server.domain.run.entity.Task
import org.graphoenix.server.domain.run.valueobject.*
import org.graphoenix.server.domain.workspace.valueobject.WorkspaceId
import org.graphoenix.server.presentation.http.controller.dto.*
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime
import java.util.*
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

fun getWorkspaceId(token: String): String =
  given()
    .`when`()
    .header("authorization", token)
    .get("/private/workspaces/me")
    .asString()

fun serializeAndCompress(
  dto: Any,
  objectMapper: ObjectMapper,
): ByteArray {
  val json = objectMapper.writeValueAsString(dto)
  val outputStream = ByteArrayOutputStream()
  GZIPOutputStream(outputStream).bufferedWriter().use { it.write(json) }
  return outputStream.toByteArray()
}

fun buildRun(
  workspaceId: WorkspaceId,
  linkId: LinkId = LinkId("link-id"),
) = Run {
  id = RunId("run-id")
  this@Run.workspaceId = workspaceId
  command = "nx test apps/server"
  status = 0
  startTime = LocalDateTime.now()
  endTime = LocalDateTime.now()
  branch = "main"
  runGroup = "default"
  inner = false
  distributedExecutionId = null
  ciExecutionId = null
  ciExecutionEnv = null
  machineInfo = MachineInfo("machine-id", "linux", "1.0", 4)
  meta = mapOf("nxCloudVersion" to "123")
  vcsContext = null
  this@Run.linkId = linkId
  projectGraph = null
  hashedContributors = null
  sha = "c4a5be0"
}

fun buildTask(
  runId: RunId = RunId("run-id"),
  workspaceId: WorkspaceId = WorkspaceId("workspace-id"),
) = Task {
  taskId = TaskId("task-id")
  this@Task.runId = runId
  this@Task.workspaceId = workspaceId
  hash = Hash("hash-value")
  projectName = "apps/server"
  target = "test"
  startTime = LocalDateTime.now()
  endTime = LocalDateTime.now()
  cacheStatus = CacheStatus.CACHE_MISS
  status = 0
  uploadedToStorage = true
  terminalOutputUploadedToFileStorage = true
  isCacheable = true
  parallelism = true
  params = "params"
  terminalOutput = "terminal-output"
  hashDetails =
    HashDetails(
      nodes = mapOf("apps/server:ProjectConfiguration" to "dummy"),
      runtime = emptyMap(),
      implicitDeps = emptyMap(),
    )
  artifactId = ArtifactId("artifact-id")
  meta =
    Metadata(
      description = "test",
      technologies = null,
      targetGroups = null,
    )
}

fun prepareExistingArtifact(
  token: String,
  objectMapper: ObjectMapper,
  linkId: String? = null,
): RunDto.End =
  buildEndRunDto(
    listOf(buildTaskDto("existing")),
    linkId,
  ).let { dto ->
    given()
      .header("authorization", token)
      .header("Content-Type", "application/octet-stream")
      .body(
        serializeAndCompress(
          dto,
          objectMapper,
        ),
      ).`when`()
      .post("/runs/end")
      .then()
      .statusCode(200)
    dto
  }

fun buildEndRunDto(
  tasks: Collection<RunDto.End.Task>,
  linkId: String? = null,
): RunDto.End =
  RunDto.End(
    branch = null,
    runGroup = "run-group",
    ciExecutionId = null,
    ciExecutionEnv = null,
    MachineInfo(
      machineId = "junit",
      platform = "test",
      version = "1",
      cpuCores = 1,
    ),
    meta = mapOf("nxCloudVersion" to "123"),
    vcsContext = null,
    tasks = tasks,
    linkId = linkId,
    projectGraph = null,
    projectGraphSha = null,
    hashedContributors = null,
    run =
      RunDto.End.RunData(
        command = "nx run apps/server:test",
        startTime = LocalDateTime.now(),
        endTime = LocalDateTime.now().plusHours(1),
        branch = null,
        runGroup = null,
        inner = false,
        distributedExecutionId = null,
        sha = null,
      ),
    clientInstanceId = UUID.randomUUID(),
    clientInstanceSource = "CLOUD_RUNNER",
  )

fun buildTaskDto(
  suffix: String,
  status: Int = 0,
): RunDto.End.Task =
  RunDto.End.Task(
    taskId = "task-$suffix",
    hash = "hash-$suffix",
    projectName = "project-$suffix",
    target = "target-$suffix",
    startTime = LocalDateTime.now(),
    endTime = LocalDateTime.now(),
    cacheStatus = "cache-miss",
    status = status,
    uploadedToStorage = true,
    isCacheable = true,
    parallelism = true,
    terminalOutputUploadedToFileStorage = false,
    params = "params-$suffix",
    terminalOutput = "terminal output",
    hashDetails =
      HashDetails(
        nodes = emptyMap(),
        runtime = emptyMap(),
        implicitDeps = emptyMap(),
      ),
    artifactId = UUID.randomUUID(),
    meta = null,
  )
