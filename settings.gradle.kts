rootProject.name = "spring-fu-build"

include("bootstraps",
		"core",
		"dependencies",
		"docs",
		"modules",
		"modules:logging",
		"modules:dynamic-configuration",
		"modules:jackson",
		"modules:mongodb",
		"modules:mongodb-coroutine",
		"modules:mustache",
		"modules:test",
		"modules:webflux",
		"modules:webflux-coroutine",
		"modules:webflux-netty",
		"modules:webflux-tomcat",
		"modules:webflux-undertow",
		"samples:coroutine-webapp",
		"samples:minimal-webapp",
		"samples:reactive-webapp")

