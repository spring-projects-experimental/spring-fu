dependencies {
	api(project(":core"))
	api("org.springframework:spring-webflux")

	testImplementation("org.junit.jupiter:junit-jupiter-api")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
	testImplementation("org.springframework:spring-test")
	testImplementation("io.projectreactor:reactor-test")

	testImplementation("io.projectreactor.netty:reactor-netty")
	testImplementation("org.apache.tomcat.embed:tomcat-embed-core")
	testImplementation("io.undertow:undertow-core")
	testImplementation("org.eclipse.jetty:jetty-server")
	testImplementation("org.eclipse.jetty:jetty-webapp")
}
