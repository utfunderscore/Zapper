package revxrsal.zapper.gradle

interface RepositoryDsl {
    fun mavenCentral() = maven("https://repo.maven.apache.org/maven2/")
    fun jitpack() = maven("https://jitpack.io/")
    fun maven(url: String)
    fun useProjectRepositories()
}

internal class BasicRepositoryDsl : RepositoryDsl {
    val repositories = mutableListOf<String>()
    var projectRepositories = false

    override fun maven(url: String) {
        repositories.add(url)
    }

    override fun useProjectRepositories() {
        projectRepositories = true
    }
}

data class Relocation(
    val pattern: String,
    val newPattern: String
)
