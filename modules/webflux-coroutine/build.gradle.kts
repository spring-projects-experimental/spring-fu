import org.jetbrains.kotlin.gradle.dsl.Coroutines

val coroutinesVersion: String by project

dependencies {
	api(project(":modules:webflux"))
	api("org.jetbrains.kotlinx:kotlinx-coroutines-core")
	api("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
	constraints {
		implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
		implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:$coroutinesVersion")
	}
}

kotlin {
	experimental.coroutines = Coroutines.ENABLE
}