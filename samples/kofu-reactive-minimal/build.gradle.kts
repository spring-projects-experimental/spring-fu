import org.gradle.kotlin.dsl.version
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.jetbrains.kotlin.jvm") version "1.3.20"
	id("io.spring.dependency-management") version "1.0.6.RELEASE"
	id("org.springframework.boot") version "2.1.2.RELEASE"
}

dependencies {
	implementation("org.springframework.fu:spring-fu-kofu:0.0.5")

	implementation("org.springframework.boot:spring-boot-starter-webflux")

	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(module = "junit")
	}
	testImplementation("org.junit.jupiter:junit-jupiter-api")
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
	if (project.hasProperty("graal")) {
		exclude(module = "netty-transport-native-epoll")
		exclude(module = "netty-transport-native-unix-common")
		exclude(module = "netty-codec-http2")
	}
}