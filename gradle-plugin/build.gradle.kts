plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        register("springFuPlugin") {
            id = "org.springframework.fu"
            implementationClass = "org.springframework.fu.gradle.SpringFuPlugin"
        }
    }
}

dependencies {
    implementation(gradleApi())
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    testImplementation(gradleTestKit())
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.hamcrest:hamcrest-core")
    testImplementation("org.junit-pioneer:junit-pioneer:0.3.0")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}
