plugins {
	id("io.spring.dependency-management")
	id("java-library")
}

java {
	sourceCompatibility = JavaVersion.VERSION_1_8
	targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
	api("org.springframework.boot:spring-boot")
	api("org.springframework.boot:spring-boot-autoconfigure")

	compileOnly("org.springframework:spring-webflux")
	compileOnly("de.flapdoodle.embed:de.flapdoodle.embed.mongo")
	compileOnly("org.springframework.data:spring-data-mongodb")
	compileOnly("org.mongodb:mongodb-driver-reactivestreams")
	compileOnly("com.fasterxml.jackson.core:jackson-databind")
	compileOnly("com.samskivert:jmustache")

	compileOnly(project(":coroutines:data-mongodb"))
	compileOnly(project(":coroutines:data-r2dbc"))
	compileOnly(project(":coroutines:webflux"))
}

repositories {
	maven("https://repo.spring.io/snapshot")
	maven("https://repo.spring.io/milestone")
}

publishing {
	publications {
		create(project.name, MavenPublication::class.java) {
			from(components["java"])
			artifactId = "spring-fu-autoconfigure-adapter"
			val sourcesJar by tasks.creating(Jar::class) {
				classifier = "sources"
				from(sourceSets["main"].allSource)
			}
			artifact(sourcesJar)
		}
	}
}
