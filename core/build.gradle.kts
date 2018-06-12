dependencies {
	api("org.springframework:spring-core")
	api("org.springframework:spring-context") {
		exclude(module = "spring-aop")
	}
	api("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	api("org.jetbrains.kotlin:kotlin-reflect")
	testImplementation("org.springframework:spring-test")
	testImplementation("org.junit.jupiter:junit-jupiter-api")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

publishing {
	publications {
		create(project.name, MavenPublication::class.java) {
			from(components["java"])
			artifactId = "spring-fu"
		}
	}
}
