plugins {
	application
	id ("com.github.johnrengelman.shadow")
}

application {
	mainClassName = "org.springframework.fu.sample.ApplicationKt"
}

dependencies {
	compile(project(":core"))
	compile(project(":modules:module-data-mongodb"))
	compile(project(":modules:module-jackson"))
	compile(project(":modules:module-mustache"))
	compile(project(":modules:module-test"))
	compile(project(":modules:module-webflux"))
}

tasks {
	"build" { dependsOn("proguard") }
}