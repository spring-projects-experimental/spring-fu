rootProject.name = "spring-fu-build"

include(
		"bootstraps",
		"coroutines",
		"coroutines:mongodb",
		"coroutines:webflux",
		"kofu",
		"docs",
		"starters",
		"starters:data-mongodb-coroutines",
		"starters:webflux-coroutines",
		"samples:coroutines",
		"samples:graal",
		"samples:reactive"
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
