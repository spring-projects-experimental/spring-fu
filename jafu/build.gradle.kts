dependencies {
	api("org.springframework.boot:spring-boot")

	implementation(project(":fuconfigure"))
	implementation("org.springframework.boot:spring-boot-autoconfigure")

	compileOnly("org.springframework:spring-webflux")
}