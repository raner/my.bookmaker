package my.book.metadata

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.itextpdf.kernel.geom.AffineTransform
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.utils.PdfMerger
import my.book.processor.Processor
import my.book.renderer.Renderer
import my.book.source.Source
import java.io.ByteArrayInputStream
import java.nio.file.FileSystems
import java.nio.file.Files

class Metadata {

	private val mapper: YAMLMapper = YAMLMapper().apply{findAndRegisterModules()}

	fun book(source: Source): Book {
        return mapper.readValue(source.reader, Book::class.java)
    }

    fun make(source: Source) {
        val result: Pair<List<ByteArray>, Int> = Pair(listOf(), 1)
        val book = book(source)
        val renderer = Renderer()
        val processor = Processor()
        val chapters = book.manuscript.chapters + book.manuscript.appendix.flatMap{it.chapters.toList()}
        chapters.fold(result) { accumulator, chapter ->
            if (chapter.file.endsWith(".md")) {
                val output = chapter.file.replace(Regex("\\.md"), ".pdf")
                val html = processor.process(source.loader.source(chapter.file).reader, source)
                val (pdf, pageCount) = renderer.render(html, accumulator.second)
                Files.write(FileSystems.getDefault().getPath("target", output), pdf)
                Pair(accumulator.first+pdf, accumulator.second+pageCount)
            }
            else if (chapter.file.endsWith(".pdf")) {
                val pageCount = source.loader.source(chapter.file).inputStream.use {
                    PdfDocument(PdfReader(it)).numberOfPages
                }
                val html = processor.blank(pageCount + pageCount.and(1), source)
                val transform: DoubleArray = chapter.transformation.toDoubleArray()
                val transformation = AffineTransform(transform)
                val (pdf, pages) =  renderer.render(html, source.loader.source(chapter.file).inputStream, transformation, accumulator.second)
                Files.write(FileSystems.getDefault().getPath("target", chapter.file), pdf)
                Pair(accumulator.first+pdf, accumulator.second+pages)
            }
            else throw IllegalArgumentException(chapter.file)
        }.first.run {
            PdfDocument(PdfWriter("target/" + source.path.replace(Regex("\\.yml"), ".pdf"))).use {
                fold(PdfMerger(it).apply {setCloseSourceDocuments(true)}) { merger, pdf ->
                    val document = PdfDocument(PdfReader(ByteArrayInputStream(pdf)))
                    merger.merge(document, 1, document.numberOfPages)
                }
            }
        }
    }
}
