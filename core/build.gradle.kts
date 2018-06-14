dependencies {
	api("org.springframework:spring-core")
	api("org.springframework:spring-context")
	api("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	api("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.slf4j:slf4j-api:1.7.25")
	testImplementation("org.springframework:spring-test")
	testImplementation("org.junit.jupiter:junit-jupiter-api")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

publishing {
	publications {
		create(project.name, MavenPublication::class.java) {
			from(components["java"])
			artifactId = "spring-fu"
			val sourcesJar by tasks.creating(Jar::class) {
				classifier = "sources"
				from(java.sourceSets["main"].allSource)
			}
			artifact(sourcesJar)
		}
	}
}
