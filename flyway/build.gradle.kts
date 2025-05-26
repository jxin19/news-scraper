plugins {
    id("org.flywaydb.flyway") version "10.18.2"
    kotlin("jvm") version "1.9.25"
}

dependencies {
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")
}
