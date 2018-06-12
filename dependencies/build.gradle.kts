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
		dependency("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
		dependency("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:$coroutinesVersion")
	}
	generatedPomCustomization {
		enabled(true)
	}
}

publishing {
	publications {
		create(project.name, MavenPublication::class.java) {
			from(components["java"])
			artifactId = "spring-fu-dependencies"
		}
	}
}