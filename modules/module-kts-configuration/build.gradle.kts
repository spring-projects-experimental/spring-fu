dependencies {
	compile(project(":core"))
	compile("org.jetbrains.kotlin:kotlin-script-runtime")
	runtime("org.jetbrains.kotlin:kotlin-script-util")
	runtime("org.jetbrains.kotlin:kotlin-compiler")

	testImplementation("org.junit.jupiter:junit-jupiter-api")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}