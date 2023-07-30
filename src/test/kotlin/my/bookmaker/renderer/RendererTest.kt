package my.bookmaker.renderer

import com.itextpdf.kernel.geom.AffineTransform
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.text.pdf.parser.PdfTextExtractor
import my.bookmaker.processor.Processor
import my.bookmaker.source.ClassLoaderSource
import my.bookmaker.source.Source
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader

class RendererTest
{
    private val empty: InputStream = ByteArrayInputStream(byteArrayOf())

    @Test
    fun testRenderer()
    {
        val resource: InputStream? = this::class.java.classLoader.getResourceAsStream("Projo.md")
        val metadata: Source = ClassLoaderSource(this::class.java.classLoader, "metadata.yml")
        val processor = Processor()
        val reader: Reader = InputStreamReader(resource?:empty)
        val html: String = processor.process(reader, metadata)
        val renderer = Renderer()
        FileOutputStream("target/output.pdf").use {
            it.write(renderer.render(html).first)
        }
        assertEquals(8, PdfDocument(PdfReader(FileInputStream("target/output.pdf"))).numberOfPages)
    }

    @Test
    fun testRendererShort() {
        val resource: InputStream? = this::class.java.classLoader.getResourceAsStream("Projo-short.md")
        val metadata: Source = ClassLoaderSource(this::class.java.classLoader, "metadata.yml")
        val processor = Processor()
        val reader: Reader = InputStreamReader(resource?:empty)
        val html: String = processor.process(reader, metadata)
        val renderer = Renderer()
        FileOutputStream("target/output.pdf").use {
            it.write(renderer.render(html).first)
        }
        assertEquals(6, PdfDocument(PdfReader(FileInputStream("target/output.pdf"))).numberOfPages)
    }

    @Test
    fun testPageNumber() {
        val resource: InputStream? = this::class.java.classLoader.getResourceAsStream("Projo-short.md")
        val metadata: Source = ClassLoaderSource(this::class.java.classLoader, "metadata.yml")
        val processor = Processor()
        val reader: Reader = InputStreamReader(resource?:empty)
        val html: String = processor.process(reader, metadata)
        val renderer = Renderer()
        FileOutputStream("target/output.pdf").use {
            it.write(renderer.render(html, 837).first)
        }
        val pdfReader = com.itextpdf.text.pdf.PdfReader(FileInputStream("target/output.pdf"))
        val text = PdfTextExtractor.getTextFromPage(pdfReader, 1)
        assertTrue(text.endsWith("| 837"))
    }

    @Test
    fun testRendererOverlay()
    {
        val metadata: Source = ClassLoaderSource(this::class.java.classLoader, "metadata.yml")
        val overlay: InputStream? = this::class.java.classLoader.getResourceAsStream("Cybersecurity.pdf")
        val transformation = AffineTransform(0.8, 0.0, 0.0, 0.8, 14.0, 12.0)
        val processor = Processor()
        val html = processor.blank(9, metadata)
        val renderer = Renderer()
        FileOutputStream("target/overlay.pdf").use {
            it.write(renderer.render(html, overlay?:empty, transformation).first)
        }
    }

    @Test
    fun testRendererOverlayPageNumber()
    {
        val metadata: Source = ClassLoaderSource(this::class.java.classLoader, "metadata.yml")
        val overlay: InputStream? = this::class.java.classLoader.getResourceAsStream("Cybersecurity.pdf")
        val transformation = AffineTransform(0.8, 0.0, 0.0, 0.8, 14.0, 12.0)
        val processor = Processor()
        val html = processor.blank(9, metadata)
        val renderer = Renderer()
        FileOutputStream("target/overlay.pdf").use {
            it.write(renderer.render(html, overlay?:empty, transformation, 837).first)
        }
        val pdfReader = com.itextpdf.text.pdf.PdfReader(FileInputStream("target/overlay.pdf"))
        val text = PdfTextExtractor.getTextFromPage(pdfReader, 1)
        assertTrue(text.endsWith("| 837"))
    }
}
