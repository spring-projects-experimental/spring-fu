import org.jetbrains.kotlin.gradle.dsl.Coroutines

dependencies {
	api("org.springframework.data:spring-data-mongodb")
	api("org.jetbrains.kotlinx:kotlinx-coroutines-core")
	api("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

	implementation("org.mongodb:mongodb-driver-reactivestreams")
}

kotlin {
	experimental.coroutines = Coroutines.ENABLE
}

publishing {
	publications {
		create(project.name, MavenPublication::class.java) {
			from(components["java"])
			artifactId = "spring-data-mongodb-coroutines"
			val sourcesJar by tasks.creating(Jar::class) {
				classifier = "sources"
				from(sourceSets["main"].allSource)
			}
			artifact(sourcesJar)
		}
	}
}
