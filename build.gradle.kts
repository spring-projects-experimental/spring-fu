import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.jetbrains.kotlin.jvm") version "1.8.22" apply false
	id("org.springframework.boot") apply false
	id("io.spring.dependency-management") version "1.1.2"
	id("maven-publish")
}

allprojects {
	apply {
		plugin("maven-publish")
		plugin("io.spring.dependency-management")
	}

	version = "0.6.0-SNAPSHOT"
	group = "org.springframework.fu"

	dependencyManagement {
		imports {
			mavenBom("org.springframework.boot:spring-boot-dependencies:${findProperty("bootVersion")}")
			mavenBom("org.testcontainers:testcontainers-bom:1.15.3")
		}
	}

	tasks.withType<Test> {
		useJUnitPlatform()
	}

	tasks.withType<JavaCompile> {
		sourceCompatibility = "17"
		targetCompatibility = "17"
	}

	tasks.withType<KotlinCompile> {
		kotlinOptions {
			freeCompilerArgs += "-Xjsr305=strict"
			jvmTarget = "17"
		}
	}

	repositories {
		mavenCentral()
		maven("https://repo.spring.io/milestone")
		maven("https://repo.spring.io/snapshot")
	}
}

fun CopySpec.setExecutablePermissions() {
	filesMatching("gradlew") { mode = 0b111101101 }
	filesMatching("gradlew.bat") { mode = 0b110100100 }
}
