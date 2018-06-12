import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.jetbrains.kotlin.jvm") version "1.2.41" apply false
	id("com.github.johnrengelman.shadow") version "2.0.4" apply false
	id("io.spring.dependency-management") version "1.0.5.RELEASE"
	id("org.asciidoctor.convert") version "1.5.6" apply false
	id("java-library")
	id("maven-publish")
}

allprojects {
	apply {
		plugin("maven-publish")
	}
	version = "1.0.0.BUILD-SNAPSHOT"
	group = "org.springframework.fu"

	publishing {
		repositories {
			val repoUsername: String? by project
			val repoPassword: String? by project
			maven {
				if (repoUsername != null && repoPassword != null) {
					credentials {
						username = repoUsername
						password = repoPassword
					}
					url = uri(if (version.toString().endsWith(".BUILD-SNAPSHOT")) "https://repo.spring.io/libs-snapshot-local/" else "https://repo.spring.io/libs-release-local/")

				} else {
					url = uri("$buildDir/repo")
				}
			}
		}
	}
}

subprojects {
	apply {
		plugin("org.jetbrains.kotlin.jvm")
		plugin("java-library")
		plugin("io.spring.dependency-management")
	}
	tasks.withType<KotlinCompile> {
		kotlinOptions {
			jvmTarget = "1.8"
			freeCompilerArgs = listOf("-Xjsr305=strict")
		}
	}
	tasks.withType<Test> {
		useJUnitPlatform()
	}
	repositories {
		mavenCentral()
		maven("https://repo.spring.io/libs-milestone")
		maven("https://repo.spring.io/snapshot")
	}
	dependencyManagement {
		val bootVersion: String by project
		val coroutinesVersion: String by project
		imports {
			mavenBom("org.springframework.boot:spring-boot-dependencies:$bootVersion")
		}
		dependencies {
			dependency("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
			dependency("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:$coroutinesVersion")
		}
		generatedPomCustomization {
			enabled(false)
		}
	}
}
