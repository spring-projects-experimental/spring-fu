plugins {
	id("io.spring.dependency-management")
	id("java-library")
}

dependencies {
	api("org.springframework.boot:spring-boot")
	api("org.springframework.boot:spring-boot-autoconfigure")

	compileOnly("org.springframework:spring-webmvc")
	compileOnly("org.springframework:spring-webflux")
	compileOnly("org.thymeleaf:thymeleaf")
	compileOnly("org.thymeleaf:thymeleaf-spring5")
	compileOnly("org.thymeleaf.extras:thymeleaf-extras-java8time")
}

repositories {
	maven("https://repo.spring.io/milestone")
}
