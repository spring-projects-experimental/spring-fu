import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.jetbrains.kotlin.jvm") version "1.3.41"
	id("io.spring.dependency-management") version "1.0.7.RELEASE"
	id("org.springframework.boot") version "2.2.0.M5"
}

dependencies {
	implementation("org.springframework.fu:spring-fu-kofu:0.2.BUILD-SNAPSHOT")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-mustache")
	implementation("org.springframework.boot:spring-boot-starter-data-cassandra-reactive")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.testcontainers:cassandra:1.11.2")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.test {
	if (project.hasProperty("isCI")) {
		exclude("com/sample/IntegrationTests")
	}
}

repositories {
	mavenLocal()
	mavenCentral()
	maven("https://repo.spring.io/milestone")
	maven("https://repo.spring.io/snapshot")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		jvmTarget = "1.8"
		freeCompilerArgs = listOf("-Xjsr305=strict", "-Xjvm-default=enable")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

configurations.all {
	exclude(module = "jakarta.validation-api")
	exclude(module = "hibernate-validator")
}
