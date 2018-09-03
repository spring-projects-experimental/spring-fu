import org.jetbrains.kotlin.gradle.dsl.Coroutines

dependencies {
	api("org.springframework.boot:spring-boot-starter-webflux")
	api(project(":coroutines:webflux"))
}

kotlin {
	experimental.coroutines = Coroutines.ENABLE
}