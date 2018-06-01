plugins {
	application
	id ("com.github.johnrengelman.shadow")
}

application {
	mainClassName = "org.springframework.fu.sample.ApplicationKt"
}

dependencies {
	compile(project(":core"))
	compile(project(":modules:jackson"))
	compile(project(":modules:mongodb"))
	compile(project(":modules:mustache"))
	compile(project(":modules:test"))
	compile(project(":modules:webflux"))
}

tasks {
	"build" { dependsOn("proguard") }
}