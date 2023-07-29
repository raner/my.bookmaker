package my.book.renderer

import com.itextpdf.kernel.geom.AffineTransform
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfPage
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.canvas.PdfCanvas
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject
import my.book.utilities.IndexedIterator
import org.xhtmlrenderer.pdf.DefaultPDFCreationListener
import org.xhtmlrenderer.pdf.ITextRenderer
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream

class Renderer
{
    fun render(html: String, pdf: OutputStream, pageNumber: Int = 1): Int {
        val renderer = ITextRenderer()
        val pageCount: Array<Int?> = arrayOfNulls(1)

        // Only add blank page at the end if the section has an odd number of pages:
        //
        renderer.listener = object: DefaultPDFCreationListener() {
            override fun preOpen(iTextRenderer: ITextRenderer?) {
                val pages: List<Any?> = renderer.rootBox.layer.pages
                pageCount[0] = pages.size.and(1.inv())
                renderer.rootBox.layer.pages = pages.subList(0, pageCount[0]?:0)
            }
        }

        // Render contents and create PDF:
        //
        renderer.setDocumentFromString(html)
        renderer.layout()
        renderer.createPDF(pdf, true, pageNumber)
        return pageCount[0]?:0
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
     * @param pageNumber the first page number (defaults to 1)
     **/
    fun render(html: String, pdfOverlay: InputStream, transformation: AffineTransform, pdf: OutputStream, pageNumber: Int = 1): Int {
        val list: List<Float> = transformation.run {listOf(scaleX, shearY, shearX, scaleY, translateX, translateY)}.map{it.toFloat()}
        val (a, b, c, d, e, f) = list
        PdfDocument(PdfReader(pdfOverlay)).use {
            overlay: PdfDocument ->
            val rendered = ByteArrayOutputStream()
            val pageCount = render(html, rendered, pageNumber)
            val input = ByteArrayInputStream(rendered.toByteArray())
            PdfDocument(PdfReader (input), PdfWriter (pdf)).use {
                original: PdfDocument ->
                val iterator: Iterator<PdfPage> = IndexedIterator(original, { it.numberOfPages }, { it: PdfDocument, page -> it.getPage(page + 1) })
                Iterable {iterator}.forEachIndexed { number, page ->
                    if (number < overlay.numberOfPages) {
                        val canvas = PdfCanvas(page.newContentStreamBefore(), page.resources, original)
                        val content: PdfFormXObject = overlay.getPage(number + 1).copyAsFormXObject(original)
                        canvas.addXObjectWithTransformationMatrix(content, a, b, c, d, e, f)
                    }
                }
            }
            return pageCount
        }
    }

    operator fun <T> List<T>.component6(): T = get(5)
}