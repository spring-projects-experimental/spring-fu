rootProject.name = "kofu"

include(
	"autoconfigure-adapter",
	"autoconfigure-adapter-cassandra",
	"autoconfigure-adapter-context",
	"autoconfigure-adapter-elasticsearch",
	"autoconfigure-adapter-jackson",
	"autoconfigure-adapter-jdbc",
	"autoconfigure-adapter-jooq",
	"autoconfigure-adapter-mongo",
	"autoconfigure-adapter-mustache",
	"autoconfigure-adapter-r2dbc",
	"autoconfigure-adapter-redis",
	"autoconfigure-adapter-security",
	"autoconfigure-adapter-thymeleaf",
	"autoconfigure-adapter-web-reactive",
	"autoconfigure-adapter-web-servlet",
	"kofu",
	"kofu-cassandra",
	"kofu-elasticsearch",
	"kofu-jdbc",
	"kofu-jooq",
	"kofu-mongo",
	"kofu-r2dbc",
	"kofu-redis",
	"kofu-templating-mustache",
	"kofu-templating-thymeleaf",
	"kofu-web",
	"kofu-webflux",
	"kofu-webmvc",
)

pluginManagement {
	repositories {
		gradlePluginPortal()
		maven("https://repo.spring.io/milestone")
		maven("https://repo.spring.io/snapshot")
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
