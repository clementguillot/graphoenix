package org.graphoenix.server.domain.run.valueobject

data class MachineInfo(
  val machineId: String,
  val platform: String,
  val version: String,
  val cpuCores: Short,
)
