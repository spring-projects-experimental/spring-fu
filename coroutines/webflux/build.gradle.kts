val coroutinesVersion: String by project

dependencies {
	api("org.springframework:spring-webflux")
	api("org.jetbrains.kotlinx:kotlinx-coroutines-core")
	api("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

	constraints {
		implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
		implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:$coroutinesVersion")
	}
}

publishing {
	publications {
		create(project.name, MavenPublication::class.java) {
			from(components["java"])
			artifactId = "spring-fu-webflux-coroutines"
			val sourcesJar by tasks.creating(Jar::class) {
				classifier = "sources"
				from(sourceSets["main"].allSource)
			}
			artifact(sourcesJar)
		}
	}
}