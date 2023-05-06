package my.book.metadata

import my.book.source.ClassLoaderSource
import my.book.source.Source
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MetadataTest
{
    lateinit var book: Book

    @BeforeEach
    fun loadResource()
    {
        val resource: Source = ClassLoaderSource(this::class.java.classLoader, "metadata.yml")
        val metadata: Metadata = Metadata()
        book = metadata.book(resource)
    }

    @Test
    fun language()
    {
        assertEquals("English", book.language)
    }

    @Test
    fun title()
    {
        assertEquals("Medical Device Software", book.title)
    }

    @Test
    fun subtitle()
    {
        assertEquals("Introduction and Selected FDA Guidance", book.subtitle)
    }

    @Test
    fun edition()
    {
        assertEquals(1, book.edition)
    }

    @Test
    fun author()
    {
        assertEquals("Mirko Raner", book.author)
    }

    @Test
    fun copyright()
    {
        assertEquals("2023 Mirko Raner", book.copyright)
    }

    @Test
    fun description()
    {
        val expected =
                "This book provides an introduction into creating software " +
                "for medical devices and an extensive appendix that " +
                "compiles current FDA guidance.\n"
        assertEquals(expected, book.description)
    }

    @Test
    fun keywords()
    {
        assertEquals(listOf("FDA", "510(k)", "software", "verification", "validation", "medical device"), book.keywords)
    }

    @Test
    fun categories()
    {
        val expected = listOf(
            "Nonfiction > Computers > Software Development & Engineering > General",
            "Nonfiction > Technology & Engineering > Biomedical")
        assertEquals(expected, book.categories)
    }

    @Test
    fun trim()
    {
        assertEquals("7.5 x 9.25 in", book.trim)
    }

    @Test
    fun bleed()
    {
        assertEquals(false, book.bleed)
    }

    @Test
    fun style()
    {
        assertEquals("style.css", book.style)
    }

    @Test
    fun coverArtwork()
    {
        assertEquals("cover.pdf", book.cover.artwork)
    }

    @Test
    fun coverFinish()
    {
        assertEquals("glossy", book.cover.finish)
    }
}
