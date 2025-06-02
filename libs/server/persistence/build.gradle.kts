import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  kotlin("jvm")
  kotlin("plugin.allopen")
  kotlin("plugin.noarg")
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
val mockkVersion: String by project
val quarkusMockkVersion: String by project

dependencies {
  implementation(kotlin("stdlib-jdk8"))
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

  implementation(enforcedPlatform("$quarkusPlatformGroupId:$quarkusPlatformArtifactId:$quarkusPlatformVersion"))
  implementation("io.quarkus:quarkus-arc")
  implementation("io.quarkus:quarkus-kotlin")
  implementation("io.quarkus:quarkus-liquibase-mongodb")
  implementation("io.quarkus:quarkus-mongodb-panache-kotlin")

  implementation(project(":libs:server:domain"))

  testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
  testImplementation("ch.tutteli.atrium:atrium-fluent:$atriumVersion")
  testImplementation("io.mockk:mockk:$mockkVersion")
  testImplementation("io.quarkiverse.mockk:quarkus-junit5-mockk:$quarkusMockkVersion")
  testImplementation("io.quarkus:quarkus-junit5")
  testImplementation("io.quarkus:quarkus-test-hibernate-reactive-panache")
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

allOpen {
  annotation("jakarta.enterprise.context.ApplicationScoped")
  annotation("jakarta.persistence.Entity")
  annotation("io.quarkus.test.junit.QuarkusTest")
}

noArg {
  annotation("io.quarkus.mongodb.panache.common.MongoEntity")
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
