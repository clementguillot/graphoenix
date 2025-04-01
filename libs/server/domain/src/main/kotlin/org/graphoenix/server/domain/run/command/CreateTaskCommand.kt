package org.graphoenix.server.domain.run.command

import org.graphoenix.server.domain.run.valueobject.ArtifactId
import org.graphoenix.server.domain.run.valueobject.CacheStatus
import org.graphoenix.server.domain.run.valueobject.Hash
import org.graphoenix.server.domain.run.valueobject.HashDetails
import java.time.LocalDateTime

data class CreateTaskCommand(
  val taskId: String,
  val hash: Hash,
  val projectName: String,
  val target: String,
  val startTime: LocalDateTime,
  val endTime: LocalDateTime,
  val cacheStatus: CacheStatus,
  val status: Int,
  val uploadedToStorage: Boolean,
  val terminalOutputUploadedToFileStorage: Boolean,
  val isCacheable: Boolean,
  val parallelism: Boolean,
  val params: String,
  val terminalOutput: String,
  val hashDetails: HashDetails,
  val artifactId: ArtifactId?,
  val meta: Map<String, String>?,
)
