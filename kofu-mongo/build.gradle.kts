plugins {
    id("org.jetbrains.kotlin.jvm")
    id("io.spring.dependency-management")
    id("java-library")
}

dependencies {
    implementation("org.springframework.boot:spring-boot")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation(project(":kofu"))
    implementation(project(":autoconfigure-adapter-mongo"))

    implementation("org.jetbrains.kotlin:kotlin-reflect")

    compileOnly("de.flapdoodle.embed:de.flapdoodle.embed.mongo.spring30x:4.7.0")
    compileOnly("org.mongodb:mongodb-driver-legacy")
    compileOnly("org.mongodb:mongodb-driver-reactivestreams")
    implementation("org.springframework.data:spring-data-mongodb")

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.springframework:spring-test")
    testImplementation("io.projectreactor:reactor-test")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.testcontainers:testcontainers")

    testImplementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    testImplementation("de.flapdoodle.embed:de.flapdoodle.embed.mongo.spring30x:4.7.0")
    testImplementation("org.mongodb:mongodb-driver-legacy")
    testImplementation("org.mongodb:mongodb-driver-reactivestreams")
}
