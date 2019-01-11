package org.springframework.fu.gradle

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junitpioneer.jupiter.TempDirectory
import java.nio.file.Files
import java.nio.file.Path


class SpringFuPluginTests {

    @Test
    @ExtendWith(TempDirectory::class)
    fun `Spring Fu plugin filters spring factories files`(@TempDirectory.TempDir tempDir: Path) {
        givenBuildScript(tempDir, """
            plugins {
                id("org.springframework.fu")
                id("java")
            }

            dependencies {
                implementation("org.springframework.boot:spring-boot-autoconfigure:2.1.1.RELEASE")
            }
        """.trimIndent())

        val output = build(tempDir, "build").output.trimEnd()
        assertThat(output, containsString("Applying SpringFuPlugin."))
        assertThat(output, containsString("Creating SpringFuArtifactTransform."))
        assertThat(output, containsString("Filtering"))
    }

    private fun build(tempDir: Path, vararg arguments: String): BuildResult =
            GradleRunner
                    .create()
                    .withProjectDir(tempDir.toFile())
                    .withPluginClasspath()
                    .withArguments(*arguments)
                    //.withDebug(true)
                    .build()

    private fun givenBuildScript(tempDir: Path, script: String) =
            Files.write(tempDir.resolve("build.gradle.kts"), script.toByteArray())

}