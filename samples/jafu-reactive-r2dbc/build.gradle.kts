plugins {
	id("io.spring.dependency-management") version "1.0.7.RELEASE"
	id("org.springframework.boot") version "2.2.0.BUILD-SNAPSHOT"
	id("java")
}

java {
	sourceCompatibility = JavaVersion.VERSION_11
	targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
	implementation("org.springframework.fu:spring-fu-jafu:0.0.6.BUILD-SNAPSHOT")

	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-mustache")
	implementation("org.springframework.data:spring-data-r2dbc:1.0.0.BUILD-SNAPSHOT")
	implementation("io.r2dbc:r2dbc-spi:1.0.0.M7")
	implementation("io.r2dbc:r2dbc-h2:1.0.0.M7")

	testImplementation("org.junit.jupiter:junit-jupiter-api")
	testImplementation("org.springframework:spring-test")
	testImplementation("io.projectreactor:reactor-test")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

repositories {
	mavenLocal()
	mavenCentral()
	maven("https://repo.spring.io/milestone")
	maven("https://repo.spring.io/snapshot")
}

configurations.all {
	exclude(module = "jakarta.validation-api")
	exclude(module = "hibernate-validator")
}
