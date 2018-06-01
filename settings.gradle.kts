rootProject.name = "spring-fu-build"

include("core",
		"modules:dynamic-configuration",
		"modules:jackson",
		"modules:mongodb",
		"modules:mustache",
		"modules:test",
		"modules:webflux",
		"samples:simple-webapp")
