package revxrsal.zapper.gradle

import org.gradle.api.Action
import org.gradle.api.Project

val Project.zapper: ZapperExtension get() = extensions.getByName("zapper") as ZapperExtension

fun Project.zapper(configure: Action<ZapperExtension>) {
    project.extensions.configure("zapper", configure)
}
