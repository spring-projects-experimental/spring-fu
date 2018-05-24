dependencies {
	compile("org.springframework:spring-core")
	compile("org.springframework:spring-context") {
		exclude(module = "spring-aop")
	}
	compile("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	compile("org.jetbrains.kotlin:kotlin-reflect")

	testImplementation("org.junit.jupiter:junit-jupiter-api")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}