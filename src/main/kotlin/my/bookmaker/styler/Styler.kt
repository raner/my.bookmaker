package my.bookmaker.styler

import my.bookmaker.metadata.Book
import my.bookmaker.metadata.Metadata
import my.bookmaker.source.Source
import com.google.common.io.CharStreams

class Styler
{
    val metadata: Metadata = Metadata()

    /**
     * Provides CSS styling for a book.
     **/
    fun style(source: Source): String
    {
        val book: Book = metadata.book(source)
        val styleSource: Source = source.loader.source(book.style)
        val style: String = CharStreams.toString(styleSource.reader)
        val (width, _, height, unit) = book.trim.split(" ")
        return "@page {size: $width$unit $height$unit;}\n$style".trimEnd()
    }
}