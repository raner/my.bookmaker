package my.book.renderer

import com.itextpdf.kernel.geom.AffineTransform
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import my.book.processor.Processor
import my.book.source.ClassLoaderSource
import my.book.source.Source
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader

class RendererTest
{
    @Test
    fun testRenderer()
    {
        val resource: InputStream = this::class.java.classLoader.getResourceAsStream("Projo.md")
        val metadata: Source = ClassLoaderSource(this::class.java.classLoader, "metadata.yml")
        val processor = Processor()
        val reader: Reader = InputStreamReader(resource)
        val html: String = processor.process(reader, metadata)
        val renderer = Renderer()
        FileOutputStream("target/output.pdf").use {
            renderer.render(html, it)
        }
        assertEquals(8, PdfDocument(PdfReader(FileInputStream("target/output.pdf"))).numberOfPages)
    }

    @Test
    fun testRendererShort()
    {
        val resource: InputStream = this::class.java.classLoader.getResourceAsStream("Projo-short.md")
        val metadata: Source = ClassLoaderSource(this::class.java.classLoader, "metadata.yml")
        val processor = Processor()
        val reader: Reader = InputStreamReader(resource)
        val html: String = processor.process(reader, metadata)
        val renderer = Renderer()
        FileOutputStream("target/output.pdf").use {
            renderer.render(html, it)
        }
        assertEquals(6, PdfDocument(PdfReader(FileInputStream("target/output.pdf"))).numberOfPages)
    }

    @Test
    fun testRendererOverlay()
    {
        val metadata: Source = ClassLoaderSource(this::class.java.classLoader, "metadata.yml")
        val overlay: InputStream = this::class.java.classLoader.getResourceAsStream("Cybersecurity.pdf")
        val transformation = AffineTransform(0.8, 0.0, 0.0, 0.8, 14.0, 12.0)
        val processor = Processor()
        val html = processor.blank(9, metadata)
        val renderer = Renderer()
        FileOutputStream("target/overlay.pdf").use {
            renderer.render(html, overlay, transformation, it)
        }
    }
}