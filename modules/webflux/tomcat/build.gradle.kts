dependencies {
	compile(project(":modules:webflux"))
	compile("org.apache.tomcat.embed:tomcat-embed-core")

	testImplementation("org.junit.jupiter:junit-jupiter-api")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
	testCompile("org.springframework:spring-test")
	testCompile("io.projectreactor:reactor-test")
	testCompile("io.projectreactor.netty:reactor-netty:0.8.0.BUILD-SNAPSHOT")
}