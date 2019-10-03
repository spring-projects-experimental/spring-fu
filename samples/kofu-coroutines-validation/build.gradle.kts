import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.jetbrains.kotlin.jvm") version "1.3.50"
	id("io.spring.dependency-management") version "1.0.8.RELEASE"
	id("org.springframework.boot") version "2.2.0.RC1"
}

dependencies {
	implementation("org.springframework.fu:spring-fu-kofu:0.3.BUILD-SNAPSHOT")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
	implementation("am.ik.yavi:yavi:0.2.1")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
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
	if (project.hasProperty("graal")) {
		exclude(module = "netty-transport-native-epoll")
		exclude(module = "netty-transport-native-unix-common")
		exclude(module = "netty-codec-http2")
	}
}
