package my.bookmaker.source

import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path

class FileSystemSource(private val fileSystemLoader: FileSystemLoader, override val path: String): Source {
    constructor(cwd: Path, path: String): this(FileSystemLoader(cwd), path)
    override val inputStream: InputStream get() = Files.newInputStream(fileSystemLoader.cwd.resolve(path))
    override val loader: Loader get() = fileSystemLoader
}
