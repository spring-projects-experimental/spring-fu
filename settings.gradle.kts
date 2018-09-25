rootProject.name = "spring-fu-build"

include(
		"bootstraps",
		"coroutines:mongodb",
		"coroutines:webflux",
		"fuconfigure",
		"kofu",
		"jafu",
		"starters",
		"starters:data-mongodb-coroutines",
		"starters:webflux-coroutines",
		"samples:kofu-coroutines",
		"samples:kofu-graal",
		"samples:kofu-reactive",
		"samples:jafu-reactive"
)

// Required since Boot 2.1 Gradle plugin is not available from Gradle portal
pluginManagement {
	repositories {
		gradlePluginPortal()
		maven("https://repo.spring.io/milestone")
		maven("http://dl.bintray.com/kotlin/kotlin-eap")
	}
	resolutionStrategy {
		eachPlugin {
			if (requested.id.id == "org.springframework.boot") {
				useModule("org.springframework.boot:spring-boot-gradle-plugin:${requested.version}")
			}
			if (requested.id.id == "org.jetbrains.kotlin.jvm") {
				useModule("org.jetbrains.kotlin:kotlin-gradle-plugin:${requested.version}")
			}
		}
	}
}
