rootProject.name = "spring-fu-build"

include(
		"autoconfigure-adapter",
		"kofu",
		"jafu"
)

pluginManagement {
	repositories {
		gradlePluginPortal()
		maven("https://repo.spring.io/milestone")
		maven("https://repo.spring.io/snapshot")
		jcenter()
	}
	resolutionStrategy {
		val bootVersion: String by settings
		eachPlugin {
			if (requested.id.id == "org.springframework.boot") {
				useModule("org.springframework.boot:spring-boot-gradle-plugin:$bootVersion")
			}
		}
	}
}
