rootProject.name = "spring-fu-build"

include("core",
		"dependencies",
		"docs",
		"modules",
		"modules:logging",
		"modules:dynamic-configuration",
		"modules:jackson",
		"modules:mongodb",
		"modules:mongodb-coroutines",
		"modules:mustache",
		"modules:test",
		"modules:webflux",
		"modules:webflux-coroutines",
		"modules:webflux-netty",
		"modules:webflux-tomcat",
		"samples:coroutines-webapp",
		"samples:reactive-webapp")

