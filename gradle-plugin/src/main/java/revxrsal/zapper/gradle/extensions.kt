package revxrsal.zapper.gradle

import org.gradle.api.Action
import org.gradle.api.Project

/**
 * Returns the zapper extension configuration
 */
val Project.zapper get() = extensions.getByName("zapper") as ZapperExtension

/**
 * Configures the Zapper plugin
 */
fun Project.zapper(configure: Action<ZapperExtension>) {
    project.extensions.configure("zapper", configure)
}
