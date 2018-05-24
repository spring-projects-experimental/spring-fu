rootProject.name = "spring-fu-build"

include("core",
		"samples:simple-webapp",
		"modules:module-data-mongodb",
		"modules:module-jackson",
		"modules:module-kts-configuration",
		"modules:module-mustache",
		"modules:module-test",
		"modules:module-webflux")
