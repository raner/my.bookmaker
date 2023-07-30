package my.bookmaker.source

interface Loader
{
    fun source(path: String): Source
}
