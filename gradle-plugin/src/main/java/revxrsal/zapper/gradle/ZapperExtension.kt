package revxrsal.zapper.gradle

open class ZapperExtension {

    var libsFolder: String = "libs"
    var relocationPrefix: String = "zapperlib"
    private var _repositories = mutableListOf<String>()
    private var _relocations = mutableListOf<Relocation>()

    val repositries: List<String> get() = _repositories
    val relocations: List<Relocation> get() = _relocations

    internal var useProjectRepositories = false

    fun repositories(configure: RepositoryDsl.() -> Unit) {
        val dsl = BasicRepositoryDsl()
        dsl.mavenCentral()
        dsl.configure()
        _repositories = dsl.repositories
        useProjectRepositories = dsl.projectRepositories
    }

    override fun toString(): String {
        return "RuntimeLibsExtension(libsFolder='$libsFolder', useProjectRepositories=$useProjectRepositories, repositries=$repositries)"
    }

    internal fun toPropertiesFile(): String {
        //language=Properties
        return """
            libs-folder=${libsFolder}
            relocation-prefix=${relocationPrefix}
        """.trimIndent()
    }

    fun relocate(pattern: String, newPattern: String) {
        _relocations.add(Relocation(pattern, newPattern))
    }
}
