import kotlinx.kover.gradle.plugin.dsl.KoverProjectExtension

plugins {
    id("org.jetbrains.kotlinx.kover") version "0.9.8"
}

val isKoverTask = gradle.startParameter.taskNames.any { it.contains("kover", ignoreCase = true) }

if (isKoverTask) {
    subprojects {
        apply(plugin = "org.jetbrains.kotlinx.kover")

        extensions.configure<KoverProjectExtension>("kover") {
            useJacoco("0.8.14")
        }
    }
}

dependencies {
    if (isKoverTask) {
        kover(project(":data-service"))
        kover(project(":analytics-service"))
        kover(project(":api-gateway"))
    }
}

kover {
    useJacoco("0.8.14")

    reports {
        filters {
            excludes {
                classes(
                    "*.configuration.*",
                    "*Application*",
                    "*Dto",
                    "*Request",
                    "*Response",
                    "*Entity",
                )
            }
        }
        total {
            verify {
                rule {
                    minBound(60)
                }
            }
        }
    }
}
