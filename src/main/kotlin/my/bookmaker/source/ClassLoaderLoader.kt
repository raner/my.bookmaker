package my.bookmaker.source

class ClassLoaderLoader(val classLoader: ClassLoader): Loader
{
    override fun source(path: String): Source
    {
        return ClassLoaderSource(this, path)
    }
}
