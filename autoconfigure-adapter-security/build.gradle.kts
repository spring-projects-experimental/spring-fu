plugins {
	id("io.spring.dependency-management")
	id("java-library")
}

dependencies {
	api("org.springframework.boot:spring-boot")
	api("org.springframework.boot:spring-boot-autoconfigure")

	compileOnly("org.springframework:spring-webmvc")
	compileOnly("javax.servlet:javax.servlet-api")
	compileOnly("org.springframework:spring-webflux")
	compileOnly("org.springframework.security:spring-security-web")
	compileOnly("org.springframework.security:spring-security-config")
}

repositories {
	maven("https://repo.spring.io/milestone")
}
