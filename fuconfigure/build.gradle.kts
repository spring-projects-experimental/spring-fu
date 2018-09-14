dependencies {
	api("org.springframework.boot:spring-boot")

	implementation("org.springframework.boot:spring-boot-autoconfigure")

	compileOnly("org.springframework:spring-webflux")
	compileOnly("de.flapdoodle.embed:de.flapdoodle.embed.mongo")
	compileOnly("org.springframework.data:spring-data-mongodb")
	compileOnly("org.mongodb:mongodb-driver-reactivestreams")
	compileOnly("com.fasterxml.jackson.core:jackson-databind")
	compileOnly("com.samskivert:jmustache")
	compileOnly(project(":coroutines:mongodb"))
	compileOnly(project(":coroutines:webflux"))
}

publishing {
	publications {
		create(project.name, MavenPublication::class.java) {
			from(components["java"])
			artifactId = "spring-boot-fuconfigure"
			val sourcesJar by tasks.creating(Jar::class) {
				classifier = "sources"
				from(sourceSets["main"].allSource)
			}
			artifact(sourcesJar)
		}
	}
}
