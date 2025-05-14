package org.graphoenix.server.domain.run.command

import org.graphoenix.server.domain.run.valueobject.*
import java.time.LocalDateTime

data class CreateRunCommand(
  val command: String,
  val startTime: LocalDateTime,
  val endTime: LocalDateTime,
  val branch: String?,
  val runGroup: String,
  val inner: Boolean,
  val distributedExecutionId: String?,
  val ciExecutionId: String?,
  val ciExecutionEnv: String?,
  val machineInfo: MachineInfo,
  val meta: Map<String, String>,
  val vcsContext: VcsContext?,
  val linkId: String,
  val projectGraph: ProjectGraph?,
  val hashedContributors: Collection<String>?,
  val sha: String?,
)
