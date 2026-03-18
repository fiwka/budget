dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

include(
    "eureka-server",
    "data-service",
    "analytics-service",
    "api-gateway",
    "ai-agent",
    "mcp-server",
    "common"
)

rootProject.name = "budget"