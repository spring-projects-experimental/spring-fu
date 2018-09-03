dependencies {
	api("org.springframework.boot:spring-boot")
	api("org.springframework.boot:spring-boot-autoconfigure")
	api("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	api("org.jetbrains.kotlin:kotlin-reflect")

	compileOnly("org.springframework:spring-webflux")
	compileOnly("de.flapdoodle.embed:de.flapdoodle.embed.mongo")
	compileOnly("org.springframework.data:spring-data-mongodb")
	compileOnly("org.mongodb:mongodb-driver-reactivestreams")
	compileOnly("com.fasterxml.jackson.core:jackson-databind")
	compileOnly("com.samskivert:jmustache")
	compileOnly(project(":coroutines:mongodb"))
	compileOnly(project(":coroutines:webflux"))

	testImplementation("org.junit.jupiter:junit-jupiter-api")
	testImplementation("org.springframework:spring-test")
	testImplementation("io.projectreactor:reactor-test")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

	testImplementation("org.springframework.boot:spring-boot-starter-webflux")
	testImplementation("org.springframework.boot:spring-boot-starter-tomcat")
	testImplementation("org.springframework.boot:spring-boot-starter-undertow")
	testImplementation("org.springframework.boot:spring-boot-starter-jetty")
	testImplementation("org.springframework.boot:spring-boot-starter-mustache")
	testImplementation("org.springframework.boot:spring-boot-starter-json")
	testImplementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
	testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	testRuntimeOnly("de.flapdoodle.embed:de.flapdoodle.embed.mongo")
}

publishing {
	publications {
		create(project.name, MavenPublication::class.java) {
			from(components["java"])
			artifactId = "kofu"
			val sourcesJar by tasks.creating(Jar::class) {
				classifier = "sources"
				from(sourceSets["main"].allSource)
			}
			artifact(sourcesJar)
		}
	}
}
