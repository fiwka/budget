package buildsrc.convention

plugins {
    id("buildsrc.convention.kotlin-jvm")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("plugin.spring")
    application
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    runtimeOnly("org.springframework.boot:spring-boot-devtools")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
}

