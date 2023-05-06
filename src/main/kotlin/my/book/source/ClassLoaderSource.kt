package my.book.source

import java.io.Reader
import java.io.InputStreamReader

class ClassLoaderSource(private val classLoaderLoader: ClassLoaderLoader, private val path: String): Source
{
    constructor(classLoader: ClassLoader, path: String): this(ClassLoaderLoader(classLoader), path)

    override val reader: Reader get() = InputStreamReader(classLoaderLoader.classLoader.getResourceAsStream(path))

    override val loader: Loader get() = classLoaderLoader
}
