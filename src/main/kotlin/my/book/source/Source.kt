package my.book.source

import java.io.Reader

interface Source
{
    fun reader(): Reader
    fun loader(): Loader
}