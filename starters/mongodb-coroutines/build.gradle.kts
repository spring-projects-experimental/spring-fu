import org.jetbrains.kotlin.gradle.dsl.Coroutines

dependencies {
	api("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
	api(project(":coroutines:mongodb"))
}

kotlin {
	experimental.coroutines = Coroutines.ENABLE
}