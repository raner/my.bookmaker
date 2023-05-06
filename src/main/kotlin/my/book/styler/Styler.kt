package my.book.styler

import my.book.metadata.Book
import my.book.metadata.Metadata
import my.book.source.Source
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
        return "@page {size: $width$unit $height$unit;}\n$style"
    }
}