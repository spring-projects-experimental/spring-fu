dependencies {
	api("org.springframework:spring-core")
	api("org.springframework:spring-context")
	api("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	api("org.jetbrains.kotlin:kotlin-reflect")
	api("org.slf4j:slf4j-api")
	implementation("org.slf4j:slf4j-simple")
	implementation("org.slf4j:jul-to-slf4j")
	implementation("org.slf4j:log4j-to-slf4j")
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
