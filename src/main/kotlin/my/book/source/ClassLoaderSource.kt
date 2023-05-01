package my.book.source

import java.io.Reader
import java.io.InputStreamReader

class ClassLoaderSource(val classLoaderLoader: ClassLoaderLoader, val path: String): Source
{
    constructor(classLoader: ClassLoader, path: String): this(ClassLoaderLoader(classLoader), path)

    override fun reader(): Reader
    {
        return InputStreamReader(classLoaderLoader.classLoader.getResourceAsStream(path))
    }

    override fun loader(): Loader
    {
        return classLoaderLoader
    }
}
