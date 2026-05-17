plugins {
    `kotlin-dsl`
}

kotlin {
    jvmToolchain(24)
}

dependencies {
    implementation(libs.kotlinGradlePlugin)
    implementation(libs.kotlinSpringPlugin)
    implementation(libs.kotlinJpaPlugin)
    implementation(libs.springBootPlugin)
    implementation(libs.springDependencyManagementPlugin)
}
