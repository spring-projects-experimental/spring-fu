import org.asciidoctor.gradle.AsciidoctorTask
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.jetbrains.kotlin.jvm") version "1.2.41" apply false
	id("com.github.johnrengelman.shadow") version "2.0.4" apply false
	id("io.spring.dependency-management") version "1.0.5.RELEASE"
	id("org.asciidoctor.convert") version "1.5.6"
	id("java-library")
	id("maven-publish")
}

allprojects {
	version = "1.0.0.BUILD-SNAPSHOT"
	group = "org.springframework.fu"
}

subprojects {
	apply {
		plugin("org.jetbrains.kotlin.jvm")
		plugin("java-library")
		plugin("maven-publish")
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

// See CONTRIBUTING.adoc in order to have Kotlin syntax highlighting
tasks {
	val asciidoctor by getting(AsciidoctorTask::class) {
		sourceDir = File("src/docs/asciidoc")
		outputDir = File("build/docs")
		inputs.files(fileTree("${projectDir}") {
			include("**/*.adoc")
		})
	}
}
