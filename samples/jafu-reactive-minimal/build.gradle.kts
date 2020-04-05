plugins {
	id("io.spring.dependency-management") version "1.0.9.RELEASE"
	id("org.springframework.boot") version "2.3.0.M4"
	id("java")
}

java {
	sourceCompatibility = JavaVersion.VERSION_1_8
	targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
	implementation("org.springframework.fu:spring-fu-jafu:0.3.0.BUILD-SNAPSHOT")

	implementation("org.springframework.boot:spring-boot-starter-webflux")

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
