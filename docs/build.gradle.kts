import org.asciidoctor.gradle.AsciidoctorTask

plugins {
	id("org.asciidoctor.convert")
}

tasks {
	val asciidoctor by getting(AsciidoctorTask::class) {
		sourceDir = File("src/docs/asciidoc")
		outputDir = File("$buildDir/docs")
		inputs.files(fileTree("$projectDir") {
			include("**/*.adoc")
		})
	}
}

publishing {
	publications {
		create("reference", MavenPublication::class.java) {
			artifactId = "spring-fu-docs"
			artifact(File("$buildDir/docs/html5/reference.html")) {
				classifier = "reference"
				builtBy(tasks.getByName("asciidoctor"))
			}
		}
	}
}