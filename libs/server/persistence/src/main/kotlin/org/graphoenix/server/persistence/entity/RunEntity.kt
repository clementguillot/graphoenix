package org.graphoenix.server.persistence.entity

import io.quarkus.mongodb.panache.common.MongoEntity
import org.bson.types.ObjectId
import java.time.LocalDateTime

@MongoEntity(collection = "run")
data class RunEntity(
  var id: ObjectId?,
  var workspaceId: ObjectId,
  var command: String,
  var status: String,
  var startTime: LocalDateTime,
  var endTime: LocalDateTime,
  var branch: String?,
  var runGroup: String,
  var inner: Boolean,
  var distributedExecutionId: String?,
  var ciExecutionId: String?,
  var ciExecutionEnv: String?,
  var machineInfo: MachineInfo,
  var meta: Map<String, String>,
  var vcsContext: VcsContext?,
  var linkId: String,
  var projectGraph: ProjectGraph?,
  var hashedContributors: Collection<String>?,
  var sha: String?,
) {
  @MongoEntity
  data class MachineInfo(
    var machineId: String = "",
    var platform: String = "",
    var version: String = "",
    var cpuCores: Short = 0,
  )

  @MongoEntity
  data class VcsContext(
    var branch: String,
    var ref: String?,
    var title: String?,
    var headSha: String?,
    var baseSha: String?,
    var commitLink: String?,
    var author: String?,
    var authorUrl: String?,
    var authorAvatarUrl: String?,
    var repositoryUrl: String?,
    var platformName: String?,
  )

  @MongoEntity
  data class ProjectGraph(
    var nodes: Map<String, Project>,
    var dependencies: Map<String, List<Dependency>>,
  ) {
    @MongoEntity
    data class Project(
      var type: String,
      var name: String,
      var data: ProjectConfiguration,
    ) {
      @MongoEntity
      data class ProjectConfiguration(
        var root: String,
        var sourceRoot: String?,
        var targets: Map<String, String>?,
        var metadata: MetadataEntity?,
      )
    }

    @MongoEntity
    data class Dependency(
      var source: String,
      var target: String,
      var type: String,
    )
  }
}
