plugins {
    id("org.jetbrains.kotlin.jvm")
    id("io.spring.dependency-management")
    id("java-library")
}

dependencies {
    implementation("org.springframework.boot:spring-boot")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation(project(":kofu"))
    implementation(project(":kofu-web"))
    implementation(project(":kofu-templating-mustache"))
    implementation(project(":kofu-templating-thymeleaf"))
    implementation(project(":autoconfigure-adapter-security"))
    implementation(project(":autoconfigure-adapter-web-servlet"))

    implementation("org.jetbrains.kotlin:kotlin-reflect")

    compileOnly("javax.servlet:javax.servlet-api")
    implementation("org.springframework:spring-webmvc")
    implementation("org.springframework.security:spring-security-web")
    implementation("org.springframework.security:spring-security-config")

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.springframework:spring-test")
    testImplementation("io.projectreactor:reactor-test")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.testcontainers:testcontainers")

    testImplementation("javax.servlet:javax.servlet-api")
    testImplementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.springframework.boot:spring-boot-starter-security")
    testImplementation("org.springframework.security:spring-security-test")

    testImplementation(project(":kofu-templating-mustache"))
    testImplementation(project(":kofu-templating-thymeleaf"))
}
