plugins {
    kotlin("jvm") version "1.6.10"
}

val kotlinVersion = "1.6.10"
val springVersion = "2.6.3"
val jacksonVersion = "2.13.1"
val kofuVersion = "0.5.0"

group = "com.sample"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = uri("https://repo.spring.io/milestone")
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web:$springVersion")
    implementation("org.springframework.fu:spring-fu-kofu:$kofuVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    implementation(kotlin("stdlib-jdk8"))

    testImplementation("org.springframework.boot:spring-boot-starter-test:$springVersion")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
