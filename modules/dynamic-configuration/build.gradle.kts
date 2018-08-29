dependencies {
	api(project(":core"))

	implementation("org.jetbrains.kotlin:kotlin-script-runtime")

	runtimeOnly("org.jetbrains.kotlin:kotlin-script-util")
	runtimeOnly("org.jetbrains.kotlin:kotlin-compiler")

	testImplementation("org.junit.jupiter:junit-jupiter-api")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}
