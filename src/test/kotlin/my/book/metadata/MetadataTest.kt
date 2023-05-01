package my.book.metadata

import org.junit.jupiter.api.Test
import my.book.source.ClassLoaderSource
import my.book.source.Source
import org.junit.jupiter.api.Assertions.assertEquals

class MetadataTest
{
    @Test
    fun loadMetadata()
    {
        val resource: Source = ClassLoaderSource(this::class.java.classLoader, "metadata.yml")
        val metadata: Metadata = Metadata()
        val book: Book = metadata.book(resource)
		assertEquals("English", book.language())
    }
}