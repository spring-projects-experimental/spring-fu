dependencies {
	api(project(":modules:webflux"))

	implementation("org.apache.tomcat.embed:tomcat-embed-core")

	testImplementation("org.junit.jupiter:junit-jupiter-api")
	testImplementation("org.springframework:spring-test")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("io.projectreactor.netty:reactor-netty:0.8.0.BUILD-SNAPSHOT")

	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

base {
	archivesBaseName = "spring-fu-webflux-tomcat"
}