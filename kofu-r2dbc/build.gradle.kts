plugins {
    id("org.jetbrains.kotlin.jvm")
    id("io.spring.dependency-management")
    id("java-library")
}

dependencies {
    implementation("org.springframework.boot:spring-boot")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation(project(":kofu"))
    implementation(project(":autoconfigure-adapter-r2dbc"))

    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("org.springframework.data:spring-data-r2dbc")

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.springframework:spring-test")
    testImplementation("io.projectreactor:reactor-test")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.testcontainers:testcontainers")

    testImplementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    testImplementation("org.springframework:spring-r2dbc")
    testRuntimeOnly("io.r2dbc:r2dbc-h2")
    testRuntimeOnly("io.r2dbc:r2dbc-postgresql:0.8.4.RELEASE")
}
