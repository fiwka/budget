plugins {
    id("buildsrc.convention.spring-boot")
}

dependencies {
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    implementation("com.github.ben-manes.caffeine:caffeine")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${libs.versions.springCloud.get()}")
    }
}

application {
    mainClass = "xyz.fiwka.budget.gateway.ApiGatewayApplicationKt"
}
