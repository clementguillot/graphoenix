import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  kotlin("jvm")
  id("io.quarkus")
  id("org.jetbrains.kotlinx.kover")
  id("com.diffplug.spotless")
}

repositories {
  mavenCentral()
  mavenLocal()
}

val javaVersion: String by project
val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project
val ktlintVersion: String by project
val atriumVersion: String by project

dependencies {
  implementation(enforcedPlatform("$quarkusPlatformGroupId:$quarkusPlatformArtifactId:$quarkusPlatformVersion"))
  implementation("io.quarkus:quarkus-kotlin")

  testImplementation("ch.tutteli.atrium:atrium-fluent:$atriumVersion")
  testImplementation("io.quarkus:quarkus-junit5")
}

group = "org.graphoenix.server"
version = "0.7.1"

java {
  sourceCompatibility = JavaVersion.toVersion(javaVersion)
  targetCompatibility = JavaVersion.toVersion(javaVersion)
}

tasks.withType<Jar> {
  archiveBaseName = "${project.group}.${project.name}"
}

tasks.withType<Test> {
  systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
  finalizedBy("koverXmlReport")
}

kotlin {
  compilerOptions {
    freeCompilerArgs.add("-Xjsr305=strict")
    jvmTarget = JvmTarget.fromTarget(javaVersion)
    javaParameters = true
  }
}

spotless {
  kotlin {
    ktlint(ktlintVersion)
  }

  kotlinGradle {
    target("*.gradle.kts")
    ktlint(ktlintVersion)
  }
}
