import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  kotlin("jvm")
  kotlin("plugin.allopen")
  id("io.quarkus")
  id("com.diffplug.spotless")
  id("org.jetbrains.kotlinx.kover")
}

repositories {
  mavenCentral()
  mavenLocal()
}

val javaVersion: String by project
val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project
val quarkusGoogleCloudServicesVersion: String by project
val ktlintVersion: String by project
val atriumVersion: String by project
val mockkVersion: String by project
val testContainerVersion: String by project

dependencies {
  implementation(enforcedPlatform("$quarkusPlatformGroupId:$quarkusPlatformArtifactId:$quarkusPlatformVersion"))
  implementation("io.quarkus:quarkus-arc")
  implementation("io.quarkus:quarkus-kotlin")
  implementation(platform("io.quarkiverse.googlecloudservices:quarkus-google-cloud-services-bom:$quarkusGoogleCloudServicesVersion"))
  implementation("io.quarkiverse.googlecloudservices:quarkus-google-cloud-storage")

  implementation(project(":libs:server:storage:core"))

  testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
  testImplementation("ch.tutteli.atrium:atrium-fluent:$atriumVersion")
  testImplementation("io.mockk:mockk:$mockkVersion")
  testImplementation("io.quarkus:quarkus-config-yaml")
  testImplementation("io.quarkus:quarkus-junit5")
  testImplementation("org.testcontainers:testcontainers:$testContainerVersion")
}

group = "org.graphoenix.server.storage"
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

allOpen {
  annotation("jakarta.enterprise.context.ApplicationScoped")
  annotation("io.quarkus.test.junit.QuarkusTest")
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
