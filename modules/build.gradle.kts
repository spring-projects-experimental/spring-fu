subprojects {
	publishing {
		publications {
			create(project.name, MavenPublication::class.java) {
				from(components["java"])
				groupId = "org.springframework.fu"
				artifactId = "${base.archivesBaseName}"
				val sourcesJar by tasks.creating(Jar::class) {
					classifier = "sources"
					from(sourceSets["main"].allSource)
				}
				artifact(sourcesJar)
			}
		}
	}
	base {
		archivesBaseName = "spring-fu-${project.name}"
	}
}
