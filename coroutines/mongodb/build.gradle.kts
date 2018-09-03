import org.jetbrains.kotlin.gradle.dsl.Coroutines

dependencies {
	api("org.springframework.data:spring-data-mongodb")
	api("org.jetbrains.kotlinx:kotlinx-coroutines-core")
	api("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
	implementation("org.mongodb:mongodb-driver-reactivestreams")
}

kotlin {
	experimental.coroutines = Coroutines.ENABLE
}
