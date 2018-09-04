val reactiveWebapp by task<Zip> {
	from("reactive") {
		exclude("build", ".gradle", ".idea", "out", "*.iml")
	}
	into("reactive")
	setExecutablePermissions()
}

val coroutineWebapp by task<Zip> {
	from("coroutines") {
		exclude("build", ".gradle", ".idea", "out", "*.iml")
	}
	into("coroutines")
	setExecutablePermissions()
}

publishing {
	publications {
		create("reactive", MavenPublication::class.java) {
			groupId = "org.springframework.fu"
			artifactId = "reactive-bootstrap"
			artifact(reactiveWebapp)
		}
		create("coroutines", MavenPublication::class.java) {
			groupId = "org.springframework.fu"
			artifactId = "coroutines-bootstrap"
			artifact(coroutineWebapp)
		}
	}
}

fun CopySpec.setExecutablePermissions() {
	filesMatching("gradlew") { mode = 0b111101101 }
	filesMatching("gradlew.bat") { mode = 0b110100100 }
}

inline fun <reified T : Task> task(noinline configuration: T.() -> Unit) = tasks.creating(T::class, configuration)