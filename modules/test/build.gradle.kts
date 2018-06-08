import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlin_version: String by extra
buildscript {
	var kotlin_version: String by extra
	kotlin_version = "1.2.41"
	repositories {
		mavenCentral()
	}
	dependencies {
		classpath(kotlinModule("gradle-plugin", kotlin_version))
	}
}
dependencies {
	compile("org.junit.jupiter:junit-jupiter-api")
	compile("org.junit.jupiter:junit-jupiter-engine")
	compile("org.springframework:spring-test")
	compile("io.projectreactor:reactor-test")
	compile(kotlinModule("stdlib-jdk8", kotlin_version))
}
apply {
	plugin("kotlin")
}
repositories {
	mavenCentral()
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
	jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
	jvmTarget = "1.8"
}