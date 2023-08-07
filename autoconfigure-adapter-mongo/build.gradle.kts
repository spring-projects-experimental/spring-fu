plugins {
	id("io.spring.dependency-management")
	id("java-library")
}

dependencies {
	api("org.springframework.boot:spring-boot")
	api("org.springframework.boot:spring-boot-autoconfigure")

	compileOnly("org.mongodb:mongodb-driver-legacy")
	compileOnly("org.mongodb:mongodb-driver-reactivestreams")
	compileOnly("org.springframework.data:spring-data-mongodb")
}

repositories {
	maven("https://repo.spring.io/milestone")
}
