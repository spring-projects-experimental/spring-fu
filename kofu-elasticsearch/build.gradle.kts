plugins {
    id("org.jetbrains.kotlin.jvm")
    id("io.spring.dependency-management")
    id("java-library")
}

dependencies {
    implementation("org.springframework.boot:spring-boot")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation(project(":kofu"))
    implementation(project(":autoconfigure-adapter-elasticsearch"))

    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("org.springframework.data:spring-data-elasticsearch")
    implementation("org.elasticsearch.client:elasticsearch-rest-high-level-client:7.17.5") {
        exclude("commons-logging:commons-logging")
    }

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.springframework:spring-test")
    testImplementation("io.projectreactor:reactor-test")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.testcontainers:testcontainers")

    testImplementation("org.springframework.data:spring-data-elasticsearch")
    testImplementation("org.elasticsearch.client:elasticsearch-rest-high-level-client:7.17.5") {
        exclude("commons-logging:commons-logging")
    }
}
