val reactiveWebapp by task<Zip> {
	from("reactive-webapp") {
		exclude("build", ".gradle", ".idea", "out")
	}
	into("reactive-webapp")
	setExecutablePermissions()
}

publishing {
	publications {
		create("reactive-webapp", MavenPublication::class.java) {
			groupId = "org.springframework.fu.bootstrap"
			artifactId = "reactive-webapp"
			artifact(reactiveWebapp)
		}
	}
}

fun CopySpec.setExecutablePermissions() {
	filesMatching("gradlew") { mode = 0b111101101 }
	filesMatching("gradlew.bat") { mode = 0b110100100 }
}

inline fun <reified T : Task> task(noinline configuration: T.() -> Unit) = tasks.creating(T::class, configuration)