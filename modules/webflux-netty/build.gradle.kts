dependencies {
	api(project(":modules:webflux"))

	implementation("io.projectreactor.netty:reactor-netty")

	testImplementation(project(":modules:webflux-test-common"))

	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}
