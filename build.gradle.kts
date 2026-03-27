import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.gradle.api.artifacts.ProjectDependency

plugins {
  id("org.jetbrains.kotlin.jvm") apply false
  id("io.github.khalilou88.jnxplus")
}

// Override the jnxplus projectDependencyTask to fix ConcurrentModificationException
// in jnxplus-gradle-plugin:1.0.0 when iterating over configurations.
tasks.named("projectDependencyTask") {
  actions.clear()
  doLast {
    val outputFilePath = (this as io.github.khalilou88.jnxplus.ProjectDependencyTask).outputFile.get()

    fun addProjects(projects: MutableList<Map<String, Any?>>, parentProjectName: String, currentProject: Project) {
      val hasBuildGradle = currentProject.file("build.gradle").exists()
      val hasBuildGradleKts = currentProject.file("build.gradle.kts").exists()
      var effectiveParent = parentProjectName

      if (hasBuildGradle || hasBuildGradleKts) {
        var projectName = currentProject.name
        val projectJsonFile = currentProject.file("project.json")
        val isProjectJsonExists = projectJsonFile.exists()
        if (isProjectJsonExists) {
          projectName = (JsonSlurper().parse(projectJsonFile) as Map<*, *>)["name"] as String
        }

        val deps = currentProject.configurations.toList()
          .flatMap { config -> config.dependencies.toList() }
          .filterIsInstance<ProjectDependency>()
          .filter { it.name != currentProject.name }
          .map { dep ->
            val depProject = currentProject.project(dep.path)
            var depName = dep.name
            val depJsonFile = depProject.file("project.json")
            val isDepJsonExists = depJsonFile.exists()
            if (isDepJsonExists) {
              depName = (JsonSlurper().parse(depJsonFile) as Map<*, *>)["name"] as String
            }
            mapOf(
              "relativePath" to currentProject.rootProject.relativePath(depProject.projectDir),
              "name" to depName,
              "isProjectJsonExists" to isDepJsonExists,
              "isBuildGradleExists" to depProject.file("build.gradle").exists()
            )
          }

        projects.add(mapOf(
          "relativePath" to currentProject.rootProject.relativePath(currentProject.projectDir),
          "name" to projectName,
          "isProjectJsonExists" to isProjectJsonExists,
          "isBuildGradleExists" to hasBuildGradle,
          "isBuildGradleKtsExists" to hasBuildGradleKts,
          "isSettingsGradleExists" to currentProject.file("settings.gradle").exists(),
          "isSettingsGradleKtsExists" to currentProject.file("settings.gradle.kts").exists(),
          "isGradlePropertiesExists" to currentProject.file("gradle.properties").exists(),
          "parentProjectName" to parentProjectName,
          "dependencies" to deps
        ))

        if (currentProject.file("settings.gradle").exists() || currentProject.file("settings.gradle.kts").exists()) {
          effectiveParent = projectName
        }
      }

      currentProject.childProjects.toMap().forEach { (_, child) ->
        addProjects(projects, effectiveParent, child)
      }
    }

    val projects = mutableListOf<Map<String, Any?>>()
    addProjects(projects, "", project)

    val result = mapOf("pluginVersion" to "1.0.0", "projects" to projects)
    val json = JsonOutput.prettyPrint(JsonOutput.toJson(result))
    File(outputFilePath).apply { parentFile.mkdirs(); writeText(json) }
  }
}
