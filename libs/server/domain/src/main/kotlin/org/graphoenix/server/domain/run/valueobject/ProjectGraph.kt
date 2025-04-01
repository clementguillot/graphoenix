package org.graphoenix.server.domain.run.valueobject

/**
 * Some type definitions can be found here:
 * https://github.com/nrwl/nx/blob/master/packages/nx/src/config/project-graph.ts
 * https://github.com/nrwl/nx/blob/master/packages/nx/src/config/workspace-json-project-json.ts
 */
data class ProjectGraph(
  val nodes: Map<String, Project>,
  val dependencies: Map<String, List<Dependency>>,
) {
  data class Project(
    val type: String,
    val name: String,
    val data: ProjectConfiguration,
  ) {
    data class ProjectConfiguration(
      val root: String,
      val sourceRoot: String?,
      val targets: Map<String, TargetConfiguration>?,
      val metadata: ProjectMetadata?,
    ) {
      data class TargetConfiguration(
        val executor: String?,
        val command: String?,
        val outputs: Collection<String>?,
        val dependsOn: Collection<String>?,
        val inputs: Collection<Any>?,
        val options: Any?,
        val configurations: Map<String, Any>?,
        val defaultConfiguration: String?,
        val cache: Boolean?,
        val parallelism: Boolean?,
        val syncGenerators: Collection<String>?,
        // missing `metadata?`
      )

      data class ProjectMetadata(
        val description: String?,
        val technologies: Collection<String>?,
        val targetGroups: Map<String, Collection<String>>?,
        // missing `owners?`
      )
    }
  }

  data class Dependency(
    val source: String,
    val target: String,
    val type: String,
  )
}
