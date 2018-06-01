dependencies {
	compile(project(":core"))
	compile("org.springframework:spring-webflux")
	compile("io.projectreactor.netty:reactor-netty:0.8.0.BUILD-SNAPSHOT")
	compileOnly("org.apache.tomcat.embed:tomcat-embed-core")
	testCompile("org.apache.tomcat.embed:tomcat-embed-core")

	testImplementation("org.junit.jupiter:junit-jupiter-api")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
	testCompile("org.springframework:spring-test")
	testCompile("io.projectreactor:reactor-test")
}