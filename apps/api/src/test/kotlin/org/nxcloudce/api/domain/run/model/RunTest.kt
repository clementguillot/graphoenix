package org.nxcloudce.api.domain.run.model

import ch.tutteli.atrium.api.fluent.en_GB.its
import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.verbs.expect
import org.junit.jupiter.api.Test
import org.nxcloudce.api.domain.workspace.model.WorkspaceId
import java.time.LocalDateTime

class RunTest {
  @Test
  fun `should build a new instance of Run`() {
    val dummyStartTime = LocalDateTime.now()
    val dummyEndTime = dummyStartTime.plusHours(1)
    val run =
      Run {
        id = RunId("run-id")
        workspaceId = WorkspaceId("workspace-id")
        command = "nx test apps/api"
        status = RunStatus.SUCCESS
        startTime = dummyStartTime
        endTime = dummyEndTime
        branch = "main"
        runGroup = "default"
        inner = false
        distributedExecutionId = null
        ciExecutionId = null
        ciExecutionEnv = null
        machineInfo = MachineInfo("machine-id", "linux", "1.0", 4)
        meta = mapOf("nxCloudVersion" to "123")
        vcsContext = buildVcsContext()
        tasks = emptyList()
        linkId = "link-id"
        projectGraph = buildProjectGraph()
        hashedContributors = null
        sha = "c4a5be0"
      }

    expect(run) {
      its { id.value }.toEqual("run-id")
      its { workspaceId.value }.toEqual("workspace-id")
      its { command }.toEqual("nx test apps/api")
      its { status }.toEqual(RunStatus.SUCCESS)
      its { startTime }.toEqual(dummyStartTime)
      its { endTime }.toEqual(dummyEndTime)
      its { branch }.toEqual("main")
      its { runGroup }.toEqual("default")
      its { inner }.toEqual(false)
      its { distributedExecutionId }.toEqual(null)
      its { ciExecutionId }.toEqual(null)
      its { ciExecutionEnv }.toEqual(null)
      its { machineInfo }.toEqual(MachineInfo("machine-id", "linux", "1.0", 4))
      its { meta }.toEqual(mapOf("nxCloudVersion" to "123"))
      its { vcsContext }.toEqual(buildVcsContext())
      its { tasks }.toEqual(emptyList())
      its { linkId }.toEqual("link-id")
      its { projectGraph }.toEqual(buildProjectGraph())
      its { hashedContributors }.toEqual(null)
      its { sha }.toEqual("c4a5be0")
    }
  }

  private fun buildVcsContext(): VcsContext =
    VcsContext(
      branch = "test",
      ref = null,
      title = null,
      headSha = null,
      baseSha = null,
      commitLink = null,
      author = null,
      authorUrl = null,
      authorAvatarUrl = null,
      repositoryUrl = "https://github.com/example/repo.git",
      platformName = null,
    )

  private fun buildProjectGraph(): ProjectGraph =
    ProjectGraph(
      nodes =
        mapOf(
          "apps/api" to
            ProjectGraph.Node(
              type = "application",
              name = "apps/api",
              data = mapOf("root" to "apps/api", "sourceRoot" to "apps/api/src"),
            ),
        ),
      dependencies =
        mapOf(
          "apps/api" to
            listOf(
              ProjectGraph.Dependency(
                source = "apps/api",
                target = "apps/api",
                type = "static",
              ),
            ),
        ),
    )
}
