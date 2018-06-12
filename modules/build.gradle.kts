subprojects {
	publishing {
		publications {
			create(project.name, MavenPublication::class.java) {
				from(components["java"])
				artifactId = "${base.archivesBaseName}"
				val sourcesJar by tasks.creating(Jar::class) {
					classifier = "sources"
					from(java.sourceSets["main"].allSource)
				}
				artifact(sourcesJar)
			}
		}
	}
	base {
		archivesBaseName = "spring-fu-${project.name}"
	}
}
