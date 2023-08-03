plugins {
	id("org.jetbrains.kotlin.jvm")
	id("io.spring.dependency-management")
	id("org.jetbrains.dokka")
	id("java-library")
}

dependencies {
	implementation("org.springframework.boot:spring-boot")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

	implementation(project(":autoconfigure-adapter-context"))
	implementation("org.jetbrains.kotlin:kotlin-reflect")

	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")

	testImplementation("org.junit.jupiter:junit-jupiter-api")
	testImplementation("org.springframework:spring-test")
	testImplementation("io.projectreactor:reactor-test")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
	testImplementation("io.mockk:mockk:1.9")
	testImplementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	testImplementation("org.testcontainers:testcontainers")
}
