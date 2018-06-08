dependencies {
	api(project(":core"))
	api("org.springframework.data:spring-data-mongodb") {
		exclude(module = "org.mongodb:mongo-java-driver")
		exclude(module = "org.slf4j:jcl-over-slf4j")
	}
	api("org.mongodb:mongodb-driver")
	api("org.mongodb:mongodb-driver-async")
	api("org.mongodb:mongodb-driver-reactivestreams")
	api("io.projectreactor:reactor-core")
}
