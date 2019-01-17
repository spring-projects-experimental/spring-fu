import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.gradle.kotlin.dsl.version
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.jetbrains.kotlin.jvm") version "1.3.11"
	id("io.spring.dependency-management") version "1.0.6.RELEASE"
	id("org.springframework.boot") version "2.1.2.RELEASE"
}

dependencies {
	implementation("org.springframework.fu:spring-fu-kofu:0.0.5-BUILD-SNAPSHOT")

	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-mustache")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.springframework.fu:spring-fu-webflux-coroutines:0.0.5-BUILD-SNAPSHOT")
	implementation("org.springframework.fu:spring-fu-data-r2dbc-coroutines:0.0.5-BUILD-SNAPSHOT")
	implementation("io.r2dbc:r2dbc-h2:1.0.0.M6")

	testImplementation("org.junit.jupiter:junit-jupiter-api")
	testImplementation("org.springframework:spring-test")
	testImplementation("io.projectreactor:reactor-test")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
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
	exclude(module = "javax.annotation-api")
	exclude(module = "hibernate-validator")
}
