import org.asciidoctor.gradle.AsciidoctorTask
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "1.2.41"
	id("io.spring.dependency-management") version "1.0.5.RELEASE"
	id("com.github.johnrengelman.shadow") version "2.0.4" apply false
	id("org.asciidoctor.convert") version "1.5.6"
}

version = "1.0.0.BUILD-SNAPSHOT"

subprojects {
	apply {
		plugin("io.spring.dependency-management")
		plugin("org.jetbrains.kotlin.jvm")
        plugin("java-library")
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
		maven("https://repo.spring.io/libs-release")
		maven("https://repo.spring.io/snapshot")
		maven("https://dl.bintray.com/konrad-kaminski/maven")
	}
	dependencyManagement {
		imports {
			mavenBom("org.springframework.boot:spring-boot-dependencies:2.0.2.RELEASE") {
				bomProperty("spring.version", "5.1.0.BUILD-SNAPSHOT")
				bomProperty("reactor-bom.version", "Californium-BUILD-SNAPSHOT")
			}
		}
		dependencies {
			val coroutinesVersion = "0.22.5"
			dependency("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
			dependency("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:$coroutinesVersion")
		}
	}
}

// See CONTRIBUTING.adoc in order to have Kotlin syntax highlighting
tasks.withType<AsciidoctorTask> {
	sourceDir = File("src/docs/asciidoc")
	outputDir = File("build/docs")
}
