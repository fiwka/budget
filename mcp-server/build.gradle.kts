plugins {
    id("buildsrc.convention.spring-boot")
}

dependencies {
    implementation(platform("org.springframework.ai:spring-ai-bom:2.0.0-M5"))
    implementation("org.springframework:spring-webflux")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    implementation("org.springframework.cloud:spring-cloud-starter-loadbalancer")
    implementation("org.springframework.ai:spring-ai-starter-mcp-server-webmvc")
    implementation("com.github.ben-manes.caffeine:caffeine")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${libs.versions.springCloud.get()}")
    }
}

application {
    mainClass = "xyz.fiwka.budget.mcpserver.McpServerApplicationKt"
}
