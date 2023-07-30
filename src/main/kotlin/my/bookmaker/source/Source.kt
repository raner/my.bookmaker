package my.bookmaker.source

import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader

interface Source {
    val path: String
    val inputStream: InputStream
    val loader: Loader
    val reader: Reader get() = InputStreamReader(inputStream)
}