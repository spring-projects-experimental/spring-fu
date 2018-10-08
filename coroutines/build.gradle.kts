import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.jetbrains.kotlin.jvm")
	id("io.spring.dependency-management")
	id("java-library")
}

allprojects {

	apply {
		plugin("org.jetbrains.kotlin.jvm")
		plugin("io.spring.dependency-management")
		plugin("java-library")
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

	repositories {
		mavenCentral()
		maven("https://repo.spring.io/milestone")
		maven("http://dl.bintray.com/kotlin/kotlin-eap")
		maven("https://jcenter.bintray.com")
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
	}

}
