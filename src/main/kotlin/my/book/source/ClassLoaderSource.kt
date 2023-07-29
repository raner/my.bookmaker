package my.book.source

import java.io.InputStream

class ClassLoaderSource(private val classLoaderLoader: ClassLoaderLoader, override val path: String): Source {
    constructor(classLoader: ClassLoader, path: String): this(ClassLoaderLoader(classLoader), path)
    override val inputStream: InputStream get() = classLoaderLoader.classLoader.getResourceAsStream(path)?:ByteArray(0).inputStream()
    override val loader: Loader get() = classLoaderLoader
}
