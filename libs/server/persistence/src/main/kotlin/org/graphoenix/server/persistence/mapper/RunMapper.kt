package org.graphoenix.server.persistence.mapper

import com.fasterxml.jackson.databind.ObjectMapper
import org.bson.types.ObjectId
import org.graphoenix.server.domain.run.command.CreateRunCommand
import org.graphoenix.server.domain.run.entity.Run
import org.graphoenix.server.domain.run.valueobject.*
import org.graphoenix.server.domain.run.valueobject.RunId
import org.graphoenix.server.domain.workspace.valueobject.WorkspaceId
import org.graphoenix.server.persistence.entity.RunEntity

fun RunEntity.toDomain(objectMapper: ObjectMapper): Run =
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
          projectGraph.nodes.let {
            it.mapValues { (_, node) ->
              ProjectGraph.Project(
                type = node.type,
                name = node.name,
                data =
                  ProjectGraph.Project.ProjectConfiguration(
                    root = node.data.root,
                    sourceRoot = node.data.sourceRoot,
                    targets =
                      node.data.targets?.let { target ->
                        target.mapValues { (_, serializedTarget) ->
                          objectMapper.readValue(
                            serializedTarget,
                            ProjectGraph.Project.ProjectConfiguration.TargetConfiguration::class.java,
                          )
                        }
                      },
                    metadata =
                      node.data.metadata?.let { metadata ->
                        ProjectGraph.Project.ProjectConfiguration.ProjectMetadata(
                          description = metadata.description,
                          technologies = metadata.technologies,
                          targetGroups = metadata.targetGroups,
                        )
                      },
                  ),
              )
            }
          },
          dependencies =
            projectGraph.dependencies.let {
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

fun CreateRunCommand.toEntity(
  status: RunStatus,
  workspaceId: WorkspaceId,
  objectMapper: ObjectMapper,
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
            projectGraph.nodes.mapValues { (_, node) ->
              RunEntity.ProjectGraph.Project(
                type = node.type,
                name = node.name,
                data =
                  RunEntity.ProjectGraph.Project.ProjectConfiguration(
                    root = node.data.root,
                    sourceRoot = node.data.sourceRoot,
                    targets =
                      node.data.targets?.let {
                        it.mapValues { (_, target) ->
                          objectMapper.writeValueAsString(target)
                        }
                      },
                    metadata =
                      node.data.metadata?.let {
                        RunEntity.ProjectGraph.Project.ProjectConfiguration.Metadata(
                          description = it.description,
                          technologies = it.technologies,
                          targetGroups = it.targetGroups,
                        )
                      },
                  ),
              )
            },
          dependencies =
            projectGraph.dependencies.mapValues { (_, dependencies) ->
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
