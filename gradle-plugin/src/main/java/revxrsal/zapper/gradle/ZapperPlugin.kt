package revxrsal.zapper.gradle

import com.github.jengelman.gradle.plugins.shadow.ShadowPlugin
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.hasPlugin
import org.gradle.kotlin.dsl.withType
import java.io.File

const val PLUGIN_VERSION: String = "0.0.1"

class ZapperPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.extensions.create("zapper", ZapperExtension::class.java)

        val runtimeLib = project.configurations.create("zap") {
            isCanBeResolved = true
            isCanBeConsumed = false
            description = "Marks a dependency for downloading at runtime"
        }

        project.afterEvaluate {
            configurations.getByName("compileOnly").extendsFrom(runtimeLib)
        }

        val outputDir = project.layout.buildDirectory.asFile.get().resolve("zapper")
        val configFile = outputDir.resolve("config.properties")
        project.tasks.register("generateZapperFiles") {
            group = "build"
            description = "Generates information about dependencies to install and relocate at runtime"
            doLast {
                outputDir.mkdirs()

                val extension = project.zapper
                project.createRepositoriesFile(outputDir, extension)

                if (project.plugins.hasPlugin(ShadowPlugin::class)) {
                    project.createRelocationsFile(outputDir, extension)
                }

                createZappersFile(outputDir, runtimeLib)

                configFile.writeText(extension.toPropertiesFile())
            }
        }

        project.addZapperDependencies()

        project.tasks.withType(Jar::class.java).configureEach {
            dependsOn("generateZapperFiles")

            from(outputDir) {
                include("dependencies.txt")
                include("relocations.txt")
                include("repositories.txt")
                include("config.properties")
                into("zapper")
            }
        }
    }
}

private fun Project.createRelocationsFile(outputDir: File, extension: ZapperExtension) {
    val relocationsFile = outputDir.resolve("relocations.txt")
    project.plugins.withId("com.github.johnrengelman.shadow") {
        val relocationRules = mutableListOf<String>()
        project.tasks.withType<ShadowJar>().configureEach {
            extension.relocations.forEach {
                relocationRules.add("${it.pattern}:${extension.relocationPrefix}.${it.newPattern}")
                relocate(it.pattern, "${extension.relocationPrefix}.${it.newPattern}")
            }
        }
        relocationsFile.writeText(relocationRules.joinToString("\n"))
    }
}

private fun createZappersFile(outputDir: File, runtimeLib: Configuration) {
    val runtimeLibsFile = outputDir.resolve("dependencies.txt")
    val runtimeLibDependencies = runtimeLib.resolvedConfiguration
        .resolvedArtifacts
        .joinToString("\n") { it.moduleVersion.id.toString() }

    runtimeLibsFile.writeText(runtimeLibDependencies)
}

private fun Project.createRepositoriesFile(outputDir: File, extension: ZapperExtension) {
    val repositoriesFile = outputDir.resolve("repositories.txt")
    val repositories = extension.repositries.toMutableSet()
    if (extension.useProjectRepositories) {
        project.repositories.forEach {
            if (it is MavenArtifactRepository) {
                repositories.add(it.url.toString())
            }
        }
    }
    repositoriesFile.writeText(repositories.joinToString("\n"))
}

private fun Project.addZapperDependencies() {
    dependencies.add("implementation", "io.github.revxrsal:zapper.api:${PLUGIN_VERSION}")
}
