plugins {
	id("io.spring.dependency-management")
	id("java-library")
}

dependencies {
	api("org.springframework.boot:spring-boot")
	api("org.springframework.boot:spring-boot-autoconfigure")

	compileOnly("org.springframework.data:spring-data-elasticsearch")
	compileOnly("org.elasticsearch.client:elasticsearch-rest-high-level-client:7.17.5") {
		exclude("commons-logging:commons-logging")
	}
}

repositories {
	maven("https://repo.spring.io/milestone")
}
