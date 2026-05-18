plugins {
    `kotlin-dsl`
}

kotlin {
    jvmToolchain(25)
}

dependencies {
    implementation(libs.kotlinGradlePlugin)
    implementation(libs.kotlinSpringPlugin)
    implementation(libs.kotlinJpaPlugin)
    implementation(libs.springBootPlugin)
    implementation(libs.springDependencyManagementPlugin)
}
