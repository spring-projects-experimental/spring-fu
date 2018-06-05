import org.jetbrains.kotlin.gradle.dsl.Coroutines

dependencies {
	compile(project(":modules:mongodb"))
	compile("org.jetbrains.kotlinx:kotlinx-coroutines-core")
	compile("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
}

kotlin {
	experimental.coroutines = Coroutines.ENABLE
}