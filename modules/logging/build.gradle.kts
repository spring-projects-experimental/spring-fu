dependencies {
	api(project(":core"))
	api("ch.qos.logback:logback-classic")

	implementation("org.apache.logging.log4j:log4j-to-slf4j")
	implementation("org.slf4j:jul-to-slf4j")

	testImplementation("org.junit.jupiter:junit-jupiter-api")

	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}
