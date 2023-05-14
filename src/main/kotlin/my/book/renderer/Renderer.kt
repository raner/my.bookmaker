package my.book.renderer

import com.itextpdf.kernel.geom.AffineTransform
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfPage
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.canvas.PdfCanvas
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import org.xhtmlrenderer.pdf.ITextRenderer
import my.book.utilities.IndexedIterator

class Renderer
{
    fun render(html: String, pdf: OutputStream)
    {
        val renderer = ITextRenderer()
        renderer.setDocumentFromString(html)
        renderer.layout()
        renderer.createPDF(pdf)
    }

    /**
     * Renders a styled HTML source and overlays a secondary PDF document.
     * The resulting output document will have as many pages as either the PDF output
     * produced by rendering the HTML or the secondary PDF document, whichever has
     * fewer pages. The output document will have the same page dimensions as the
     * document produced by rendering the styled HTML.
     *
     * @param html styled HTML content (possibly just empty pages)
     * @param pdfOverlay a secondary PDF document to be overlaid page by page
     * @param transformation an affine transformation for the overlay content
     * @param pdf the final output PDF stream
     **/
    fun render(html: String, pdfOverlay: InputStream, transformation: AffineTransform, pdf: OutputStream) {
        val list: List<Float> = transformation.run{listOf(scaleX, shearY, shearX, scaleY, translateX, translateY)}.map{it.toFloat()}
        val (a, b, c, d, e, f) = list
        PdfDocument(PdfReader(pdfOverlay)).use {
            overlay: PdfDocument ->
            val rendered = ByteArrayOutputStream()
            render(html, rendered)
            val input = ByteArrayInputStream(rendered.toByteArray())
            PdfDocument(PdfReader (input), PdfWriter (pdf)).use {
                original: PdfDocument ->
                val iterator: Iterator<PdfPage> = IndexedIterator(original, { it.numberOfPages }, { it: PdfDocument, page -> it.getPage(page + 1) })
                val pages: Iterable<PdfPage> = Iterable {iterator}
                pages.forEachIndexed { number, page ->
                    val canvas = PdfCanvas(page.newContentStreamBefore(), page.resources, original)
                    val content: PdfFormXObject = overlay.getPage(number + 1).copyAsFormXObject(original)
                    canvas.addXObjectWithTransformationMatrix(content, a, b, c, d, e, f)
                }
            }
        }
    }

    operator fun <T> List<T>.component6(): T = get(5)
}