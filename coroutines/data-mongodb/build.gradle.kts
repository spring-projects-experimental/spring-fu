dependencies {
	api("org.springframework.data:spring-data-mongodb")
	api("org.jetbrains.kotlinx:kotlinx-coroutines-core")
	api("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

	implementation("org.mongodb:mongodb-driver-reactivestreams")
}

publishing {
	publications {
		create(project.name, MavenPublication::class.java) {
			from(components["java"])
			artifactId = "spring-fu-data-mongodb-coroutines"
			val sourcesJar by tasks.creating(Jar::class) {
				classifier = "sources"
				from(sourceSets["main"].allSource)
			}
			artifact(sourcesJar)
		}
	}
}
