package org.graphoenix.server.presentation.cli

import io.quarkus.runtime.QuarkusApplication
import kotlinx.coroutines.runBlocking
import org.graphoenix.server.application.run.usecase.CleanupRun
import org.graphoenix.server.application.run.usecase.CleanupRunRequest
import org.jboss.logging.Logger
import java.time.LocalDateTime

class CleanupCommand(
  private val cleanupRun: CleanupRun,
) : QuarkusApplication {
  companion object {
    private val logger = Logger.getLogger(CleanupCommand::class.java)
  }

  override fun run(vararg args: String): Int =
    runBlocking {
      val days = args[0].toInt()
      logger.info("Cleanup started! Removing runs that are older than $days days")
      cleanupRun(
        CleanupRunRequest(creationDateThreshold = LocalDateTime.now().minusDays(days.toLong())),
      ) {
        logger.info("Cleanup ended! # of deleted runs: ${it.deletedCount}")
      }
      0
    }
}
