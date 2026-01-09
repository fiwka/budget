val postgresHost = System.getenv("POSTGRES_HOST") ?: "localhost"
val postgresUser = System.getenv("POSTGRES_USER") ?: "postgres"
val postgresPassword = System.getenv("POSTGRES_PASSWORD") ?: "postgres"
val postgresDatabase = System.getenv("POSTGRES_DATABASE") ?: "budget_cw"
val postgresPort = System.getenv("POSTGRES_PORT") ?: "5432"
val postgresUrl = "jdbc:postgresql://${postgresHost}:${postgresPort}/${postgresDatabase}"

val liquibase by configurations.creating

dependencies {
    liquibase(libs.liquibaseCore)
    liquibase(libs.liquibaseGroovyDsl)
    liquibase(libs.picocli)
    liquibase(libs.snakeyaml)
    liquibase(libs.postgresql)
    liquibase(libs.jaxb)
}

val migrationTask =
    tasks.register("migrate", JavaExec::class) {
        classpath = liquibase
        mainClass = "liquibase.integration.commandline.LiquibaseCommandLine"
        workingDir = projectDir

        args("update")
        args("--changelog-file=src/main/db/master.yaml")
        args("--url=${postgresUrl}")
        args("--username=${postgresUser}")
        args("--password=${postgresPassword}")
        args("--log-level=debug")
    }