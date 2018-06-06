dependencies {
	compile(project(":core"))
	compile("ch.qos.logback:logback-classic")
 	compile("org.apache.logging.log4j:log4j-to-slf4j")
	compile("org.slf4j:jul-to-slf4j")

	testImplementation("org.junit.jupiter:junit-jupiter-api")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}