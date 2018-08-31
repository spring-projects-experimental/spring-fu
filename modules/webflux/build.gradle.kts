dependencies {
	api(project(":core"))
	api("org.springframework.boot:spring-boot-starter-webflux") {
		exclude(module = "spring-boot-starter-json")
		exclude(module = "hibernate-validator")
	}

	testImplementation("org.junit.jupiter:junit-jupiter-api")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
	testImplementation("org.springframework:spring-test")
	testImplementation("io.projectreactor:reactor-test")

	testImplementation("org.springframework.boot:spring-boot-starter-tomcat")
	testImplementation("org.springframework.boot:spring-boot-starter-undertow")
	testImplementation("org.springframework.boot:spring-boot-starter-jetty")
}
