import org.jetbrains.kotlin.gradle.dsl.Coroutines

dependencies {
	api(project(":modules:webflux"))
	api("org.jetbrains.kotlinx:kotlinx-coroutines-core")
	api("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
}

kotlin {
	experimental.coroutines = Coroutines.ENABLE
}
