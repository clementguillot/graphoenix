package org.nxcloudce.server.persistence.gateway

import org.bson.types.ObjectId
import org.nxcloudce.server.domain.run.model.*
import org.nxcloudce.server.domain.run.usecase.EndRunRequest
import org.nxcloudce.server.domain.workspace.model.WorkspaceId
import org.nxcloudce.server.persistence.entity.RunEntity

fun RunEntity.toDomain(): Run =
  Run {
    id = RunId(this@toDomain.id.toString())
    workspaceId = WorkspaceId(this@toDomain.workspaceId.toString())
    command = this@toDomain.command
    status = RunStatus.valueOf(this@toDomain.status)
    startTime = this@toDomain.startTime
    endTime = this@toDomain.endTime
    branch = this@toDomain.branch
    runGroup = this@toDomain.runGroup
    inner = this@toDomain.inner
    distributedExecutionId = this@toDomain.distributedExecutionId
    ciExecutionId = this@toDomain.ciExecutionId
    ciExecutionEnv = this@toDomain.ciExecutionEnv
    machineInfo =
      MachineInfo(
        machineId = this@toDomain.machineInfo.machineId,
        platform = this@toDomain.machineInfo.platform,
        version = this@toDomain.machineInfo.version,
        cpuCores = this@toDomain.machineInfo.cpuCores,
      )
    meta = this@toDomain.meta
    vcsContext =
      this@toDomain.vcsContext?.let { vcsContext ->
        VcsContext(
          branch = vcsContext.branch,
          ref = vcsContext.ref,
          title = vcsContext.title,
          headSha = vcsContext.headSha,
          baseSha = vcsContext.baseSha,
          commitLink = vcsContext.commitLink,
          author = vcsContext.author,
          authorUrl = vcsContext.authorUrl,
          authorAvatarUrl = vcsContext.authorAvatarUrl,
          repositoryUrl = vcsContext.repositoryUrl,
          platformName = vcsContext.platformName,
        )
      }
    linkId = this@toDomain.linkId
    projectGraph =
      this@toDomain.projectGraph?.let { projectGraph ->
        ProjectGraph(
          nodes =
            projectGraph.nodes?.let {
              it.mapValues { (_, node) ->
                ProjectGraph.Project(
                  type = node.type,
                  name = node.name,
                  data =
                    ProjectGraph.Project.Data(
                      root = node.data.root,
                      sourceRoot = node.data.sourceRoot,
                      metadata = null,
                      targets = emptyMap(),
                      // TODO: can't map those two fields for now
                      // https://github.com/clementguillot/nx-cloud-ce/issues/118
                      // metadata = node.data.metadata,
                      // targets =
                      //   node.data.targets.mapValues { (_, target) ->
                      //     ProjectGraph.Project.Data.Target(
                      //       executor = target.executor,
                      //       dependsOn = target.dependsOn,
                      //       options = target.options,
                      //       configurations = target.configurations,
                      //       parallelism = target.parallelism,
                      //       inputs = target.inputs,
                      //       outputs = target.outputs,
                      //       defaultConfiguration = target.defaultConfiguration,
                      //       cache = target.cache,
                      //     )
                      //   },
                    ),
                )
              }
            },
          dependencies =
            projectGraph.dependencies?.let {
              it.mapValues { (_, dependencies) ->
                dependencies.map { dependency ->
                  ProjectGraph.Dependency(
                    source = dependency.source,
                    target = dependency.target,
                    type = dependency.type,
                  )
                }
              }
            },
        )
      }
    hashedContributors = this@toDomain.hashedContributors
    sha = this@toDomain.sha
  }

fun EndRunRequest.Run.toEntity(
  status: RunStatus,
  workspaceId: WorkspaceId,
): RunEntity =
  RunEntity(
    id = null,
    workspaceId = ObjectId(workspaceId.value),
    command = command,
    status = status.name,
    startTime = startTime,
    endTime = endTime,
    branch = branch,
    runGroup = runGroup,
    inner = inner,
    distributedExecutionId = distributedExecutionId,
    ciExecutionId = ciExecutionId,
    ciExecutionEnv = ciExecutionEnv,
    machineInfo =
      RunEntity.MachineInfo().apply {
        machineId = this@toEntity.machineInfo.machineId
        platform = this@toEntity.machineInfo.platform
        version = this@toEntity.machineInfo.version
        cpuCores = this@toEntity.machineInfo.cpuCores
      },
    meta = meta,
    vcsContext =
      vcsContext?.let { vcsContext ->
        RunEntity.VcsContext(
          branch = vcsContext.branch,
          ref = vcsContext.ref,
          title = vcsContext.title,
          headSha = vcsContext.headSha,
          baseSha = vcsContext.baseSha,
          commitLink = vcsContext.commitLink,
          author = vcsContext.author,
          authorUrl = vcsContext.authorUrl,
          authorAvatarUrl = vcsContext.authorAvatarUrl,
          repositoryUrl = vcsContext.repositoryUrl,
          platformName = vcsContext.platformName,
        )
      },
    linkId = linkId,
    projectGraph =
      projectGraph?.let { projectGraph ->
        RunEntity.ProjectGraph(
          nodes =
            projectGraph.nodes!!.mapValues { (_, node) ->
              RunEntity.ProjectGraph.Project(
                type = node.type,
                name = node.name,
                data =
                  RunEntity.ProjectGraph.Project.Data(
                    root = node.data.root,
                    sourceRoot = node.data.sourceRoot,
//                    metadata = node.data.metadata,
//                    targets =
//                      node.data.targets.mapValues { (_, target) ->
//                        RunEntity.ProjectGraph.Project.Data.Target(
//                          executor = target.executor,
//                          dependsOn = target.dependsOn,
//                          options = target.options,
//                          configurations = target.configurations,
//                          parallelism = target.parallelism,
//                          inputs = target.inputs,
//                          outputs = target.outputs,
//                          defaultConfiguration = target.defaultConfiguration,
//                          cache = target.cache,
//                        )
//                      },
                  ),
              )
            },
          dependencies =
            projectGraph.dependencies!!.mapValues { (_, dependencies) ->
              dependencies.map { dependency ->
                RunEntity.ProjectGraph.Dependency(
                  source = dependency.source,
                  target = dependency.target,
                  type = dependency.type,
                )
              }
            },
        )
      },
    hashedContributors = hashedContributors,
    sha = sha,
  )
