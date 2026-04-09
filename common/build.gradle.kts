import com.github.davidmc24.gradle.plugin.avro.GenerateAvroJavaTask
import org.gradle.api.tasks.SourceSetContainer
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("buildsrc.convention.kotlin-jvm")
    alias(libs.plugins.avro.plugin)
}

val avroSource = "src/main/avro"
val avroOutput = "build/generated/avro/java"

dependencies {
    implementation(libs.apache.avro)
}

configure<SourceSetContainer> {
    named("main") {
        java.srcDir(avroOutput)
    }
}

tasks.named<GenerateAvroJavaTask>("generateAvroJava") {
    source = fileTree(avroSource)
    setOutputDir(file(avroOutput))
}

tasks.named<KotlinCompile>("compileKotlin") {
    dependsOn(tasks.named("generateAvroJava"))
}
