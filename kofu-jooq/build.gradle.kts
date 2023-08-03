plugins {
    id("org.jetbrains.kotlin.jvm")
    id("io.spring.dependency-management")
    id("java-library")
}

dependencies {
    implementation("org.springframework.boot:spring-boot")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation(project(":kofu"))
    implementation(project(":kofu-jdbc"))
    implementation(project(":autoconfigure-adapter-jdbc"))
    implementation(project(":autoconfigure-adapter-jooq"))

    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("org.jooq:jooq")
    implementation("org.springframework.boot:spring-boot-starter-jooq")

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.springframework:spring-test")
    testImplementation("io.projectreactor:reactor-test")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.testcontainers:testcontainers")

    testImplementation("org.springframework.boot:spring-boot-starter-jooq")
}
