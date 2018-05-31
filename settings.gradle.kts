rootProject.name = "spring-fu-build"

include("core",
		"modules:data-mongodb",
		"modules:jackson",
		"modules:dynamic-configuration",
		"modules:mustache",
		"modules:test",
		"modules:webflux",
		"samples:simple-webapp")
