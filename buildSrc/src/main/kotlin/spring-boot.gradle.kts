package buildsrc.convention

import org.gradle.process.CommandLineArgumentProvider

plugins {
    id("buildsrc.convention.kotlin-jvm")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("plugin.spring")
    application
}

val mockitoAgent = configurations.create("mockitoAgent") {
    isCanBeConsumed = false
    isCanBeResolved = true
    isTransitive = false
}

class MockitoAgentArgumentProvider(
    @get:org.gradle.api.tasks.Classpath
    val classpath: org.gradle.api.file.FileCollection,
) : CommandLineArgumentProvider {
    override fun asArguments(): Iterable<String> =
        classpath.files.map { "-javaagent:${it.absolutePath}" }
}

tasks.withType<Test>().configureEach {
    jvmArgumentProviders.add(MockitoAgentArgumentProvider(mockitoAgent))
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    runtimeOnly("org.springframework.boot:spring-boot-devtools")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    mockitoAgent("org.mockito:mockito-core")
}
