plugins {
	id("io.spring.dependency-management")
	id("java-library")
}

java {
	sourceCompatibility = JavaVersion.VERSION_1_8
	targetCompatibility = JavaVersion.VERSION_1_8
}

ext["spring-security.version"] = "5.2.0.BUILD-SNAPSHOT"
ext["spring-security-config.version"] = "5.2.0.BUILD-SNAPSHOT"

dependencies {
	api("org.springframework.boot:spring-boot")
	api("org.springframework.boot:spring-boot-autoconfigure")

	compileOnly("org.springframework:spring-webflux")
	compileOnly("org.springframework.boot:spring-boot-starter-security")
}

repositories {
	maven("https://repo.spring.io/milestone")
}

publishing {
	publications {
		create(project.name, MavenPublication::class.java) {
			from(components["java"])
			artifactId = "spring-fu-security-adapter"
			val sourcesJar by tasks.creating(Jar::class) {
				classifier = "sources"
				from(sourceSets["main"].allSource)
			}
			artifact(sourcesJar)
		}
	}
}
