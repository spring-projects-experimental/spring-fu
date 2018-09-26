val reactiveWebapp by task<Zip> {
	from("kofu-reactive") {
		exclude("build", ".gradle", ".idea", "out", "*.iml")
	}
	into("kofu-reactive")
	setExecutablePermissions()
}

val coroutineWebapp by task<Zip> {
	from("kofu-coroutines") {
		exclude("build", ".gradle", ".idea", "out", "*.iml")
	}
	into("kofu-coroutines")
	setExecutablePermissions()
}

publishing {
	publications {
		create("kofu-reactive", MavenPublication::class.java) {
			groupId = "org.springframework.fu"
			artifactId = "bootstrap-kofu-reactive"
			artifact(reactiveWebapp)
		}
		create("kofu-coroutines", MavenPublication::class.java) {
			groupId = "org.springframework.fu"
			artifactId = "bootstrap-kofu-coroutines"
			artifact(coroutineWebapp)
		}
		create("kofu-graal", MavenPublication::class.java) {
			groupId = "org.springframework.fu"
			artifactId = "bootstrap-kofu-graal"
			artifact(coroutineWebapp)
		}
	}
}

fun CopySpec.setExecutablePermissions() {
	filesMatching("gradlew") { mode = 0b111101101 }
	filesMatching("gradlew.bat") { mode = 0b110100100 }
}

inline fun <reified T : Task> task(noinline configuration: T.() -> Unit) = tasks.creating(T::class, configuration)
