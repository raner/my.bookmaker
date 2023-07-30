package my.bookmaker.metadata

import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor
import com.itextpdf.kernel.pdf.canvas.parser.listener.SimpleTextExtractionStrategy
import my.bookmaker.source.ClassLoaderSource
import my.bookmaker.source.Source
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MetadataTest
{
    private lateinit var book: Book

    @BeforeEach
    fun loadResource()
    {
        val resource: Source = ClassLoaderSource(this::class.java.classLoader, "metadata.yml")
        val metadata = Metadata()
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

    @Test
    fun chapters()
    {
        val resource: Source = ClassLoaderSource(this::class.java.classLoader, "book.yml")
        val metadata = Metadata()
        book = metadata.book(resource)
        val chapters: Array<Chapter> = book.manuscript.chapters
        val appendix: Array<Appendix> = book.manuscript.appendix
        assertEquals(2, chapters.size)
        assertEquals("Projo.md", chapters[0].file)
        assertEquals("Cybersecurity-RTA.pdf", chapters[1].file)
        assertEquals(2, appendix.size)
        assertEquals("Final Guidance Documents", appendix[0].title)
        assertEquals(2, appendix[0].chapters.size)
        assertEquals("Cybersecurity.pdf", appendix[0].chapters[0].file)
        assertEquals("https://www.fda.gov/media/73065/download", appendix[0].chapters[1].url)
        assertEquals("Draft Guidance Documents", appendix[1].title)
        assertEquals(2, appendix[1].chapters.size)
        assertEquals("https://www.fda.gov/media/119933/download", appendix[1].chapters[0].url)
        assertEquals("Projo-short.md", appendix[1].chapters[1].file)
    }

    @Test
    fun shortBook()
    {
        val source: Source = ClassLoaderSource(this::class.java.classLoader, "short-book.yml")
        val metadata = Metadata()
        metadata.make(source)
        val document = PdfDocument(PdfReader("target/short-book.pdf"))
        val page = document.getPage(30)
        val text = PdfTextExtractor.getTextFromPage(page, SimpleTextExtractionStrategy())
        assertTrue(text.startsWith("|\n|\n|\n|\n|\n|\n|\n|\n30 |\nthe deserializer"))
    }
}
