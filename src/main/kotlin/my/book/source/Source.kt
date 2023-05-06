package my.book.source

import java.io.Reader

interface Source
{
    val reader: Reader
    val loader: Loader
}