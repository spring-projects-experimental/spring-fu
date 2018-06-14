dependencies {
	api(project(":modules:logging"))
	api("ch.qos.logback:logback-classic")

	testImplementation("org.junit.jupiter:junit-jupiter-api")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

configurations.all { exclude(module = "slf4j-simple") }
