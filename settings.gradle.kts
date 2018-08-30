rootProject.name = "spring-fu-build"

include(
		"bootstraps",
		"core",
		"dependencies",
		"docs",
		"modules",
		"modules:dynamic-configuration",
		"modules:mongodb",
		"modules:mongodb-coroutine",
		"modules:mongodb-embedded",
		"modules:webflux",
		"modules:webflux-coroutine",
		"modules:webflux-jackson",
		"modules:webflux-mustache",
		"modules:webflux-thymeleaf",
		"samples:coroutine-webapp",
		"samples:minimal-webapp",
		"samples:reactive-webapp"
)

// Required since Boot 2.1 Gradle plugin is not available from Gradle portal
pluginManagement {
	repositories {
		gradlePluginPortal()
		maven("https://repo.spring.io/milestone")
	}
	resolutionStrategy {
		eachPlugin {
			if (requested.id.id == "org.springframework.boot") {
				useModule("org.springframework.boot:spring-boot-gradle-plugin:${requested.version}")
			}
		}
	}
}
