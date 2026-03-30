plugins {
    id("buildsrc.convention.spring-boot")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    implementation("com.github.ben-manes.caffeine:caffeine")
    implementation("org.springframework.boot:spring-boot-starter-kafka")
    implementation(libs.mapstruct)
    implementation(project(":common"))
    testImplementation("org.springframework.kafka:spring-kafka-test")
    runtimeOnly(libs.postgresql)
    kapt(libs.mapstruct.processor)
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${libs.versions.springCloud.get()}")
    }
}

application {
    mainClass = "xyz.fiwka.budget.dataservice.DataServiceApplicationKt"
}
