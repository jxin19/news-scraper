plugins {
    // kotlin
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    kotlin("plugin.jpa") version "1.9.25"
    kotlin("kapt") version "1.9.25"

    // spring boot
    id("org.springframework.boot") version "3.4.1"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.github.davidmc24.gradle.plugin.avro") version "1.9.1"
}

allprojects {
    group = "com.ddi"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
        maven {
            url = uri("https://packages.confluent.io/maven/")
        }
    }
}

val postgresVersion = "42.7.4"
val querydslVersion = "5.1.0"
val lettuceVersion = "6.4.0.RELEASE"
val mockkVersion = "1.13.12"
val gsonVersion = "2.11.0"
val jacksonVersion = "2.15.2"

subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "kotlin-kapt")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
    }

    dependencyManagement {
        imports {
            mavenBom("org.springframework.boot:spring-boot-dependencies:3.4.1")
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:2023.0.3")
        }
    }

    dependencies {
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
        implementation("org.jetbrains.kotlin:kotlin-reflect")

        // coroutine
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

        // jpa, postgres
        implementation("org.springframework.boot:spring-boot-starter-jdbc")
        implementation("org.springframework.boot:spring-boot-starter-data-jpa")
        implementation("org.apache.tomcat:tomcat-jdbc")
        implementation("org.postgresql:postgresql:$postgresVersion")

        // querydsl
        implementation("com.querydsl:querydsl-jpa:$querydslVersion:jakarta")
        implementation("com.querydsl:querydsl-apt:$querydslVersion:jakarta")
        implementation("jakarta.persistence:jakarta.persistence-api")
        implementation("jakarta.annotation:jakarta.annotation-api")
        kapt("com.querydsl:querydsl-apt:$querydslVersion:jakarta")
        kapt("org.springframework.boot:spring-boot-configuration-processor")

        // cache
        implementation("org.springframework.boot:spring-boot-starter-cache")
        implementation("org.springframework.boot:spring-boot-starter-data-redis")
        implementation("io.lettuce:lettuce-core:$lettuceVersion")

        // kafka
        implementation("org.springframework.kafka:spring-kafka")

        // library
        implementation("com.google.code.gson:gson:${gsonVersion}")
        implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
        implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")

        // test
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
        testImplementation("io.mockk:mockk:$mockkVersion")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
        testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
    }

    tasks.test {
        useJUnitPlatform()
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

configure(subprojects.filter { it.name in listOf("scrape-rss", "scrape-web", "stream-news") }) {
    apply(plugin = "com.github.davidmc24.gradle.plugin.avro")

    val avroVersion = "1.12.0"
    val confluentVersion = "7.8.0"

    dependencies {
        implementation(project(":core"))

        // kafka
        implementation("org.springframework.kafka:spring-kafka")

        // avro
        implementation("org.apache.avro:avro:$avroVersion")
        implementation("io.confluent:kafka-avro-serializer:$confluentVersion")
        implementation("io.confluent:kafka-streams-avro-serde:$confluentVersion")
    }

    avro {
        isCreateSetters.set(false)
        isCreateOptionalGetters.set(false)
        isGettersReturnOptional.set(false)
        isOptionalGettersForNullableFieldsOnly.set(false)
        fieldVisibility.set("PRIVATE")
        outputCharacterEncoding.set("UTF-8")
        stringType.set("String")
        templateDirectory.set(null as String?)
        isEnableDecimalLogicalType.set(true)
    }
}

tasks.withType<JavaExec> {
    jvmArgs = listOf("-XX:+EnableDynamicAgentLoading")
}

tasks.withType<Test> {
    useJUnitPlatform()
    jvmArgs = listOf("-XX:+EnableDynamicAgentLoading")
}

tasks.named<org.springframework.boot.gradle.tasks.run.BootRun>("bootRun") {
    standardInput = System.`in`
}
