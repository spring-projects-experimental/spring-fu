plugins {
	id("io.spring.dependency-management") version "1.0.10.RELEASE"
	id("org.springframework.boot") version "2.4.0"
	id("java")
}

java {
	sourceCompatibility = JavaVersion.VERSION_1_8
	targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
	implementation("org.springframework.fu:spring-fu-jafu:0.5.0-SNAPSHOT")

	implementation("org.springframework.boot:spring-boot-starter-webflux")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

repositories {
	mavenLocal()
	mavenCentral()
	maven("https://repo.spring.io/milestone")
	maven("https://repo.spring.io/snapshot")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
