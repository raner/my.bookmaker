package my.bookmaker.source

import java.nio.file.Path

class FileSystemLoader(val cwd: Path): Loader {
    override fun source(path: String): Source {
        return FileSystemSource(this, path)
    }
}
