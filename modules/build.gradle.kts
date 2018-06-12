subprojects {
	publishing {
		publications {
			create(project.name, MavenPublication::class.java) {
				from(components["java"])
				artifactId = "${base.archivesBaseName}"
			}
		}
	}
	base {
		archivesBaseName = "spring-fu-${project.name}"
	}
}
