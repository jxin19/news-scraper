val jjwtVersion = "0.12.6"
val springdocVersion = "2.7.0"

dependencies {
    implementation(project(":core"))

    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")

    // jwt
    implementation("io.jsonwebtoken:jjwt-api:$jjwtVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:$jjwtVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:$jjwtVersion")

    implementation("io.opentelemetry:opentelemetry-api:1.43.0")

    // swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springdocVersion")

    // test
    testImplementation("org.springframework.security:spring-security-test")
}

configurations.all {
    resolutionStrategy {
        force("io.swagger.core.v3:swagger-annotations:2.2.27")
        force("io.swagger.core.v3:swagger-annotations-jakarta:2.2.27")
        force("io.swagger.core.v3:swagger-core-jakarta:2.2.27")
        force("io.swagger.core.v3:swagger-models-jakarta:2.2.27")
    }
}