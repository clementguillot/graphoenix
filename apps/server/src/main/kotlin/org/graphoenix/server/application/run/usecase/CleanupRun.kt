package org.graphoenix.server.application.run.usecase

import jakarta.enterprise.context.ApplicationScoped
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.graphoenix.server.application.UseCase
import org.graphoenix.server.domain.run.gateway.ArtifactRepository
import org.graphoenix.server.domain.run.gateway.RunRepository
import org.graphoenix.server.domain.run.gateway.StorageService
import org.graphoenix.server.domain.run.gateway.TaskRepository
import org.jboss.logging.Logger
import java.time.LocalDateTime

@ApplicationScoped
class CleanupRun(
  private val runRepository: RunRepository,
  private val taskRepository: TaskRepository,
  private val artifactRepository: ArtifactRepository,
  private val storageService: StorageService,
) : UseCase<CleanupRunRequest, CleanupRunResponse> {
  companion object {
    private val logger = Logger.getLogger(CleanupRun::class.java)
  }

  override suspend fun <T> invoke(
    request: CleanupRunRequest,
    presenter: (CleanupRunResponse) -> T,
  ): T {
    var deletedRunCount = 0

    runRepository.findAllByCreationDateOlderThan(request.creationDateThreshold).collect { run ->
      logger.info("Removing run ${run.id}")

      taskRepository.findAllByRunId(run.id).collect { task ->
        task.artifactId?.let { artifactId ->
          coroutineScope {
            launch { storageService.deleteArtifact(artifactId, run.workspaceId) }
            launch { artifactRepository.delete(artifactId) }
          }
        }
      }

      logger.info("Artifacts and their files have been deleted")

      coroutineScope {
        launch { taskRepository.deleteAllByRunId(run.id) }
        launch { runRepository.delete(run) }
      }

      logger.info("Tasks and run have been deleted")
      deletedRunCount++
    }

    return presenter(CleanupRunResponse(deletedCount = deletedRunCount))
  }
}

data class CleanupRunRequest(
  val creationDateThreshold: LocalDateTime,
)

data class CleanupRunResponse(
  val deletedCount: Int,
)
