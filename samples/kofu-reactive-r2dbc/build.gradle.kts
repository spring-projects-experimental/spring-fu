
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.jetbrains.kotlin.jvm") version "1.3.31"
	id("io.spring.dependency-management") version "1.0.7.RELEASE"
	id("org.springframework.boot") version "2.2.0.M3"
}

dependencies {
	implementation("org.springframework.fu:spring-fu-kofu:0.1")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-mustache")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.springframework.data:spring-data-r2dbc:1.0.0.M2")
	implementation("io.r2dbc:r2dbc-h2")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

dependencyManagement {
	imports {
		mavenBom("io.r2dbc:r2dbc-bom:Arabba-M8")
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