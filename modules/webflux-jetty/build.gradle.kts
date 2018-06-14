dependencies {
    api(project(":modules:webflux"))

    implementation("org.eclipse.jetty:jetty-server")
    implementation("org.eclipse.jetty:jetty-servlet")
    implementation("org.slf4j:slf4j-api:1.7.25")

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.springframework:spring-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("io.projectreactor.netty:reactor-netty") // For the client

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}