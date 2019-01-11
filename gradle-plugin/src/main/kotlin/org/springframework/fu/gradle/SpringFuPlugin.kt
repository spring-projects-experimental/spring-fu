package org.springframework.fu.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.transform.ArtifactTransform
import org.gradle.api.attributes.Attribute
import java.io.File

class SpringFuPlugin : Plugin<Project> {

    private val artifactType = Attribute.of("artifactType", String::class.java)
    private val minified = Attribute.of("minified", String::class.java)

    override fun apply(project: Project) = project.run {
        println("Applying SsssspringFuPlugin.")
        with(dependencies) {
            attributesSchema {
                attribute(minified)
            }
            artifactTypes.getByName("jar") {
                attributes.attribute(minified, "false")
            }
            registerTransform {
                // Based on
                from.attribute(minified, "false").attribute(artifactType, "jar")
                to.attribute(minified, "true").attribute(artifactType, "jar")
                artifactTransform(SpringFuArtifactTransform::class.java)
            }
        }

    }
    class SpringFuArtifactTransform : ArtifactTransform() {

        init {
            println("Creating SpringFuArtifactTransform.")
        }

        override fun transform(input: File) : List<File> {
            println("Filtering $input")
            return listOf(input)
        }
    }
}