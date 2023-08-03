plugins {
	id("io.spring.dependency-management")
	id("java-library")
}

dependencies {
	api("org.springframework.boot:spring-boot")
	api("org.springframework.boot:spring-boot-autoconfigure")

	compileOnly("org.springframework.data:spring-data-cassandra")
}

repositories {
	maven("https://repo.spring.io/milestone")
}
