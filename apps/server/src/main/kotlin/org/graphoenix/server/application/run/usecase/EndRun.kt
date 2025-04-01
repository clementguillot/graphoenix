package org.graphoenix.server.application.run.usecase

import jakarta.enterprise.context.ApplicationScoped
import org.graphoenix.server.application.UseCase
import org.graphoenix.server.domain.run.command.CreateRunCommand
import org.graphoenix.server.domain.run.command.CreateTaskCommand
import org.graphoenix.server.domain.run.entity.Artifact
import org.graphoenix.server.domain.run.entity.Run
import org.graphoenix.server.domain.run.entity.Task
import org.graphoenix.server.domain.run.gateway.ArtifactRepository
import org.graphoenix.server.domain.run.gateway.RunRepository
import org.graphoenix.server.domain.run.gateway.TaskRepository
import org.graphoenix.server.domain.run.valueobject.RunStatus
import org.graphoenix.server.domain.workspace.valueobject.WorkspaceId

@ApplicationScoped
class EndRun(
  private val runRepository: RunRepository,
  private val taskRepository: TaskRepository,
  private val artifactRepository: ArtifactRepository,
) : UseCase<EndRunRequest, EndRunResponse> {
  override suspend operator fun <T> invoke(
    request: EndRunRequest,
    presenter: (EndRunResponse) -> T,
  ): T {
    val run = runRepository.create(request.run, getRunStatus(request.tasks), request.workspaceId)
    val tasks = taskRepository.create(request.tasks, run.id, request.workspaceId)

    createTaskArtifacts(tasks, request.workspaceId)

    return presenter(EndRunResponse(run = run))
  }

  private fun getRunStatus(tasks: Collection<CreateTaskCommand>): RunStatus =
    when (tasks.any { it.status != 0 }) {
      true -> RunStatus.FAILURE
      false -> RunStatus.SUCCESS
    }

  private suspend fun createTaskArtifacts(
    tasks: Collection<Task>,
    workspaceId: WorkspaceId,
  ): Collection<Artifact.Exist> =
    tasks
      .filter { it.uploadedToStorage }
      .map { mapOf(it.artifactId!! to it.hash) }
      .flatMap { artifactRepository.createRemoteArtifacts(it, workspaceId) }
}

data class EndRunRequest(
  val run: CreateRunCommand,
  val tasks: Collection<CreateTaskCommand>,
  val workspaceId: WorkspaceId,
)

data class EndRunResponse(
  val run: Run,
)
