rootProject.name = "spring-fu-build"

include(
		"autoconfigure-adapter",
		"kofu",
		"jafu",
		"coroutines",
		"coroutines:data-mongodb",
		"coroutines:data-r2dbc",
		"coroutines:webflux"
)

pluginManagement {
	repositories {
		gradlePluginPortal()
		maven("https://repo.spring.io/milestone")
		maven("https://repo.spring.io/snapshot")
	}
	resolutionStrategy {
		eachPlugin {
			if (requested.id.id == "org.springframework.boot") {
				useModule("org.springframework.boot:spring-boot-gradle-plugin:${requested.version}")
			}
		}
	}
}
