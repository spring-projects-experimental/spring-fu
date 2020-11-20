rootProject.name = "spring-fu-samples"

include(
		"jafu-reactive-minimal",
		"jafu-reactive-mongodb",
		"jafu-reactive-data-r2dbc",
		"jafu-reactive-r2dbc",
		"jafu-servlet-minimal",
		"jafu-servlet-cassandra",
		"kofu-coroutines-mongodb",
		"kofu-coroutines-minimal",
		"kofu-coroutines-r2dbc",
		"kofu-coroutines-validation",
		"kofu-reactive-minimal",
		"kofu-reactive-mongodb",
		"kofu-reactive-data-r2dbc",
		"kofu-reactive-r2dbc",
		"kofu-reactive-redis",
		"kofu-reactive-cassandra",
		"kofu-reactive-validation",
		"kofu-servlet-minimal",
		"kofu-servlet-validation"
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
