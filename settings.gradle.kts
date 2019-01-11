rootProject.name = "spring-fu-build"

include(
		"gradle-plugin",
		"autoconfigure-adapter",
		"kofu",
		"jafu",
		"coroutines",
		"coroutines:data-mongodb",
		"coroutines:data-r2dbc",
		"coroutines:webflux"
)
