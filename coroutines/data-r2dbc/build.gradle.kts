dependencies {
	api("org.springframework.data:spring-data-r2dbc:1.0.0.M1")
	api("io.r2dbc:r2dbc-spi:1.0.0.M6")
	api("org.jetbrains.kotlinx:kotlinx-coroutines-core")
	api("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
}

repositories {
	maven("https://repo.spring.io/snapshot")
	maven("https://repo.spring.io/milestone")
}


publishing {
	publications {
		create(project.name, MavenPublication::class.java) {
			from(components["java"])
			artifactId = "spring-fu-data-r2dbc-coroutines"
			val sourcesJar by tasks.creating(Jar::class) {
				classifier = "sources"
				from(sourceSets["main"].allSource)
			}
			artifact(sourcesJar)
		}
	}
}
