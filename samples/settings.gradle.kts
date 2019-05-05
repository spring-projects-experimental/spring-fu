rootProject.name = "spring-fu-samples"

include(
		"kofu-coroutines-mongodb",
		"kofu-coroutines-minimal",
		"kofu-coroutines-r2dbc",
        "kofu-coroutines-validation",
		"kofu-reactive-minimal",
		"kofu-reactive-mongodb",
		"kofu-reactive-r2dbc",
		"kofu-reactive-validation"
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
