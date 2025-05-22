package org.graphoenix.server.persistence.repository

import ch.tutteli.atrium.api.fluent.en_GB.*
import ch.tutteli.atrium.api.verbs.expect
import io.quarkus.test.junit.QuarkusTest
import io.smallrye.mutiny.coroutines.awaitSuspending
import jakarta.inject.Inject
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.bson.types.ObjectId
import org.graphoenix.server.domain.run.command.CreateRunCommand
import org.graphoenix.server.domain.run.entity.Run
import org.graphoenix.server.domain.run.valueobject.*
import org.graphoenix.server.domain.workspace.valueobject.WorkspaceId
import org.graphoenix.server.persistence.entity.RunEntity
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.*

@QuarkusTest
class RunPanacheRepositoryTest {
  @Inject
  lateinit var runPanacheRepository: RunPanacheRepository

  @BeforeEach
  fun setUp() {
    runBlocking {
      runPanacheRepository.deleteAll().awaitSuspending()
    }
  }

  @Test
  fun `should find all runs by their end date from the DB`() =
    runTest {
      // Given
      val dummyRuns =
        listOf(
          buildRunEntity(endTime = LocalDateTime.now().minusMinutes(60)),
          buildRunEntity(endTime = LocalDateTime.now().minusMinutes(60)),
        )
      val thresholdDate = LocalDateTime.now()
      runPanacheRepository.persist(dummyRuns).awaitSuspending()

      // When
      val result = runPanacheRepository.findAllByCreationDateOlderThan(thresholdDate).toList()

      // Then
      expect(result.size).toEqual(2)
    }

  @Test
  fun `should find a page of runs by their workspace ID`() =
    runTest {
      // Given
      val dummyWorkspaceId = ObjectId()
      val dummyRuns =
        listOf(
          buildRunEntity(workspaceId = dummyWorkspaceId),
          buildRunEntity(workspaceId = dummyWorkspaceId),
          buildRunEntity(workspaceId = ObjectId()),
        )
      runPanacheRepository.persist(dummyRuns).awaitSuspending()

      // When
      val resultPage0 = runPanacheRepository.findPageByWorkspaceId(WorkspaceId(dummyWorkspaceId.toString()), 0, 10)
      val resultPage1 = runPanacheRepository.findPageByWorkspaceId(WorkspaceId(dummyWorkspaceId.toString()), 1, 10)

      // Then
      expect(resultPage0) {
        its { totalCount }.toEqual(2)
        its {
          items.map { it.id.value }
        }.toContain.inAnyOrder.only.elementsOf(
          dummyRuns
            .filter { it.workspaceId == dummyWorkspaceId }
            .map { it.id.toString() },
        )
        its {
          items.map { it.workspaceId.value }
        }.toContain.inAnyOrder.only.elementsOf(
          dummyRuns
            .filter { it.workspaceId == dummyWorkspaceId }
            .map { it.workspaceId.toString() },
        )
      }
      expect(resultPage1) {
        its { totalCount }.toEqual(2)
        its { items.size }.toEqual(0)
      }
    }

  @Test
  fun `should find a run by its ID and workspace ID`() =
    runTest {
      // Given
      val dummyWorkspaceId = ObjectId()
      val dummyRun = buildRunEntity(workspaceId = dummyWorkspaceId)
      runPanacheRepository.persist(dummyRun).awaitSuspending()

      // When
      val foundRun = runPanacheRepository.findByLinkId(LinkId(dummyRun.linkId))
      val notFoundRun = runPanacheRepository.findByLinkId(LinkId("random"))

      // Then
      expect(foundRun).toBeAnInstanceOf<Run>()
      expect(foundRun!!) {
        its { id.value }.toEqual(dummyRun.id.toString())
        its { linkId.value }.toEqual(dummyRun.linkId)
        its { workspaceId.value }.toEqual(dummyRun.workspaceId.toString())
      }
      expect(notFoundRun).toEqual(null)
    }

  @Test
  fun `should create a new run in the DB`() =
    runTest {
      // Given
      val workspaceId = WorkspaceId(ObjectId().toString())
      val runRequest =
        CreateRunCommand(
          command = "nx test apps/server",
          startTime = LocalDateTime.now(),
          endTime = LocalDateTime.now(),
          branch = "main",
          runGroup = "",
          inner = false,
          distributedExecutionId = null,
          ciExecutionId = null,
          ciExecutionEnv = null,
          machineInfo =
            MachineInfo(
              machineId = "machine-id",
              platform = "junit",
              version = "42",
              cpuCores = 42,
            ),
          meta = mapOf("nxCloudVersion" to "123"),
          vcsContext =
            VcsContext(
              branch = "main",
              ref = null,
              title = null,
              headSha = null,
              baseSha = null,
              commitLink = null,
              author = "clement guillot",
              authorUrl = null,
              authorAvatarUrl = null,
              repositoryUrl = "https://github.com/clementguillot/graphoenix",
              platformName = "JUNIT",
            ),
          linkId = LinkId("test-link"),
          projectGraph =
            ProjectGraph(
              nodes =
                mapOf(
                  "apps/server" to
                    ProjectGraph.Project(
                      type = "application",
                      name = "apps/server",
                      data =
                        ProjectGraph.Project.ProjectConfiguration(
                          root = "root",
                          sourceRoot = "root",
                          metadata =
                            Metadata(
                              description = "description",
                              technologies = listOf("vite"),
                              targetGroups = mapOf("test" to listOf("test")),
                            ),
                          targets =
                            mapOf(
                              "build" to
                                ProjectGraph.Project.ProjectConfiguration.TargetConfiguration(
                                  executor = "@nx/angular:ng-packagr-lite",
                                  command = null,
                                  outputs = listOf("^build", "build"),
                                  dependsOn = null,
                                  inputs = listOf("production", "^production"),
                                  options = null,
                                  configurations = null,
                                  defaultConfiguration = null,
                                  cache = null,
                                  parallelism = null,
                                  syncGenerators = null,
                                ),
                            ),
                        ),
                    ),
                ),
              dependencies =
                mapOf(
                  "apps/server" to
                    listOf(
                      ProjectGraph.Dependency(source = "apps/server", target = "libs/server/domain", type = "static"),
                    ),
                ),
            ),
          hashedContributors = null,
          sha = null,
        )

      // When
      val result = runPanacheRepository.create(runRequest, 0, workspaceId)
      val decodedEntity = runPanacheRepository.findById(ObjectId(result.id.value)).awaitSuspending()

      // Then
      expect(decodedEntity!!) {
        its { linkId }.toEqual("test-link")
        its { command }.toEqual("nx test apps/server")
      }
      expect(runPanacheRepository.count().awaitSuspending()).toEqual(1L)
    }

  @Test
  fun `should delete a run by its ID from the DB`() =
    runTest {
      // Given
      val dummyRunId = ObjectId()
      val dummyRun =
        Run {
          id(dummyRunId.toString())
          workspaceId(UUID.randomUUID().toString())
          command = "test command"
          status = 0
          startTime = LocalDateTime.now()
          endTime = LocalDateTime.now()
          runGroup = "group"
          inner = true
          machineInfo = MachineInfo("machineId", "platform", "version", 4)
          meta = emptyMap()
          linkId = LinkId("link-id")
        }
      runPanacheRepository.persist(buildRunEntity(id = dummyRunId)).awaitSuspending()

      // When
      val result = runPanacheRepository.delete(dummyRun)

      // Then
      expect(result).toEqual(true)
      expect(runPanacheRepository.count().awaitSuspending()).toEqual(0L)
    }

  fun buildRunEntity(
    id: ObjectId? = null,
    endTime: LocalDateTime = LocalDateTime.now(),
    workspaceId: ObjectId = ObjectId(),
    linkId: String = "test link id",
  ): RunEntity =
    RunEntity(
      id = id,
      workspaceId = workspaceId,
      command = "test command",
      status = 0,
      startTime = LocalDateTime.now(),
      endTime = endTime,
      branch = "test branch",
      runGroup = "test run group",
      inner = true,
      distributedExecutionId = "test distributed execution id",
      ciExecutionId = "test ci execution id",
      ciExecutionEnv = "test ci execution env",
      machineInfo = RunEntity.MachineInfo(),
      meta = mapOf("key" to "value"),
      vcsContext =
        RunEntity.VcsContext(
          branch = "test branch",
          ref = "test ref",
          title = "test title",
          headSha = "test head sha",
          baseSha = "test base sha",
          commitLink = "test commit link",
          author = "test author",
          authorUrl = "test author url",
          authorAvatarUrl = "test author avatar url",
          repositoryUrl = "test repository url",
          platformName = "test platform name",
        ),
      linkId = linkId,
      projectGraph = null,
      hashedContributors = listOf("test hashed contributors"),
      sha = "test sha",
    )
}
