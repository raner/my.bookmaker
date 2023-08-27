//                                                                            //
// My Bookmaker - Markdown-based creation of printed books                    //
// Copyright (C) 2023 Mirko Raner                                             //
//                                                                            //
// This program is free software: you can redistribute it and/or modify       //
// it under the terms of the GNU Affero General Public License as             //
// published by the Free Software Foundation, either version 3 of the         //
// License, or (at your option) any later version.                            //
//                                                                            //
// This program is distributed in the hope that it will be useful,            //
// but WITHOUT ANY WARRANTY; without even the implied warranty of             //
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the               //
// GNU Affero General Public License for more details.                        //
//                                                                            //
// You should have received a copy of the GNU Affero General Public License   //
// along with this program. If not, see <https://www.gnu.org/licenses/>.      //
//                                                                            //
package my.bookmaker.metadata

import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor
import com.itextpdf.kernel.pdf.canvas.parser.listener.SimpleTextExtractionStrategy
import my.bookmaker.source.ClassLoaderSource
import my.bookmaker.source.Source
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.security.DigestException

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
        val chapters: Array<Chapter> = book.manuscript.chapters!!
        val appendix: Array<Appendix> = book.manuscript.appendix!!
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

    @Test
    fun bookWithCorrectChecksum()
    {
        Metadata().make(ClassLoaderSource(this::class.java.classLoader, "book-with-correct-checksum.yml"))
        // No assertion, should just not throw an exception
    }

    @Test
    fun bookWithIncorrectChecksum()
    {
        val source: Source = ClassLoaderSource(this::class.java.classLoader, "book-with-incorrect-checksum.yml")
        assertThrows(DigestException::class.java) {Metadata().make(source)}
    }

    @Test
    fun bookWithToC()
    {
        val source: Source = ClassLoaderSource(this::class.java.classLoader, "book-with-toc.yml")
        val metadata = Metadata()
        metadata.make(source)
        val document = PdfDocument(PdfReader("target/book-with-toc.pdf"))
        val page = document.getPage(1)
        val text = PdfTextExtractor.getTextFromPage(page, SimpleTextExtractionStrategy())
        val expected = """
            |
            |
            |
            |
            |
            |
            |
            |
            | 1
            1.  
            1.  
            1.  
            2.  
            3.  
            4.  
            5.  
            6.  
            7.  
            8.  
            1.  
            2.  
            3.  
            4.  
            projo   1
             FAQ 3
             How do I use Projo in my project? 3
             How does Projo relate to Project Lombok? 3
             Does Projo support immutable objects? 4
             Can Projo create Value Objects? 4
             Are Java proxies efficient for implementing objects at runtime? 5
             Will Projo work with my JAX-RS application? 5
             What is new in Projo 1.1.0? 6
             What is new in Projo 1.2.0? 6
             Major Improvements for API Scraping 6
             Other Major Improvements 7
             Bug Fixes 7
             Security Vulnerability Fixes 7
        """.trimIndent()
        assertEquals(expected, text)
    }

    @Test
    fun tableOfContents()
    {
        val source: Source = ClassLoaderSource(this::class.java.classLoader, "short-book.yml")
        val metadata = Metadata()
        val toc = metadata.make(source)
        val expected = listOf(
            "projo     "                                                   to  1 to 0,
            "FAQ"                                                             to  3 to 1,
            "How do I use Projo in my project?"                               to  3 to 2,
            "How does Projo relate to Project Lombok?"                        to  3 to 2,
            "Does Projo support immutable objects?"                           to  4 to 2,
            "Can Projo create Value Objects?"                                 to  4 to 2,
            "Are Java proxies efficient for implementing objects at runtime?" to  5 to 2,
            "Will Projo work with my JAX-RS application?"                     to  5 to 2,
            "What is new in Projo 1.1.0?"                                     to  6 to 2,
            "What is new in Projo 1.2.0?"                                     to  6 to 2,
            "Major Improvements for API Scraping"                             to  6 to 3,
            "Other Major Improvements"                                        to  7 to 3,
            "Bug Fixes"                                                       to  7 to 3,
            "Security Vulnerability Fixes"                                    to  7 to 3,
            "Content of Premarket Submissions for Management of Cybersecurity in Medical Devices"
                                                                              to 9 to 0,
            "Cybersecurity in Medical Devices: Refuse to Accept Policy for Cyber Devices and Related Systems Under Section 524B of the FDC Act"
                                                                              to 19 to 0,
            "projo     "                                                   to 25 to 0,
            "FAQ"                                                             to 27 to 1,
            "How do I use Projo in my project?"                               to 27 to 2,
            "How does Projo relate to Project Lombok?"                        to 27 to 2,
            "Does Projo support immutable objects?"                           to 28 to 2,
            "Can Projo create Value Objects?"                                 to 28 to 2,
            "Are Java proxies efficient for implementing objects at runtime?" to 29 to 2,
            "Will Projo work with my JAX-RS application?"                     to 29 to 2
        )
        assertEquals(expected.map{triple(it)}, toc.toc)
    }

    private fun triple(nestedPair: Pair<Pair<String, Int>, Int>): Triple<String, Int, Int> =
        Triple(nestedPair.first.first, nestedPair.first.second, nestedPair.second)
}
