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
import java.io.FileOutputStream

class Metadata {

	private val mapper: YAMLMapper = YAMLMapper().apply{findAndRegisterModules()}

	fun book(source: Source): Book {
        return mapper.readValue(source.reader, Book::class.java)
    }

    fun make(source: Source) {
        var page = 1
        val book = book(source)
        val renderer = Renderer()
        val processor = Processor()
        val outputFiles: MutableList<String> = arrayListOf()
        val chapters = book.manuscript.chapters + book.manuscript.appendix.flatMap{it.chapters.toList()}
        for (chapter in chapters) {
            if (chapter.file.endsWith(".md")) {
                val output = "target/" + chapter.file.replace(Regex("\\.md"), ".pdf")
                val html = processor.process(source.loader.source(chapter.file).reader, source)
                val pageCount = renderer.render(html, FileOutputStream(output), page)
                page += pageCount
                outputFiles.add(output)
            }
            else if (chapter.file.endsWith(".pdf")) {
                val output = "target/" + chapter.file
                val pageCount = source.loader.source(chapter.file).inputStream.use {
                    PdfDocument(PdfReader(it)).numberOfPages
                }
                val html = processor.blank(pageCount + pageCount.and(1), source)
                val transform: DoubleArray = chapter.transformation.toDoubleArray()
                val transformation = AffineTransform(transform)
                page += renderer.render(html, source.loader.source(chapter.file).inputStream, transformation, FileOutputStream(output), page)
                outputFiles.add(output)
            }
            else throw IllegalArgumentException(chapter.file)
        }
        PdfDocument(PdfWriter("target/" + source.path.replace(Regex("\\.yml"), ".pdf"))).use {
            val merger: PdfMerger = PdfMerger(it).apply{setCloseSourceDocuments(true)}
            outputFiles.forEach { fileName ->
                val document = PdfDocument(PdfReader(fileName))
                merger.merge(document, 1, document.numberOfPages)
            }
        }
    }
}
