plugins {
	id("io.spring.dependency-management")
}

val bootVersion: String by project
val coroutinesVersion: String by project

dependencyManagement {
	imports {
		mavenBom("org.springframework.boot:spring-boot-dependencies:$bootVersion")
	}
	dependencies {
		dependency("org.springframework.fu:spring-fu:$version")
		dependency("org.springframework.fu:spring-fu-dynamic-configuration:$version")
		dependency("org.springframework.fu:spring-fu-mongodb:$version")
		dependency("org.springframework.fu:spring-fu-mongodb-coroutine:$version")
		dependency("org.springframework.fu:spring-fu-webflux:$version")
		dependency("org.springframework.fu:spring-fu-webflux-coroutine:$version")
		dependency("org.springframework.fu:spring-fu-webflux-jackson:$version")
		dependency("org.springframework.fu:spring-fu-webflux-mustache:$version")
		dependency("org.springframework.fu:spring-fu-webflux-thymeleaf:$version")
		dependency("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
		dependency("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:$coroutinesVersion")
	}
}

publishing {
	publications {
		create(project.name, MavenPublication::class.java) {
			artifactId = "spring-fu-dependencies"
		}
	}
}