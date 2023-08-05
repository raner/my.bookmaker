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

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.itextpdf.kernel.geom.AffineTransform
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.utils.PdfMerger
import my.bookmaker.processor.Processor
import my.bookmaker.renderer.Renderer
import my.bookmaker.source.Source
import java.io.ByteArrayInputStream
import java.net.URL
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
        chapters.foldIndexed(result) { index, accumulator, chapter ->
            if (chapter.url != null) {
                val url = URL(chapter.url)
                val connection = url.openConnection().apply{connect()}
                if (connection.contentType == "application/pdf") {
                    val (pageCount, title) = connection.getInputStream().use {
                        PdfDocument(PdfReader(it)).run{
                            Pair(numberOfPages, documentInfo.title)
                        }
                    }
                    val html = processor.blank(pageCount + pageCount.and(1), source)
                    val transform: DoubleArray = (chapter.transformation?:arrayOf(1.0, 0.0, 0.0, 1.0)).toDoubleArray()
                    val transformation = AffineTransform(transform)
                    val (pdf, pages) = renderer.render(html, url.openStream(), transformation, accumulator.second)
                    val path = FileSystems.getDefault().getPath("target", "$title.pdf")
                    Files.createDirectories(path.parent)
                    Files.write(path, pdf)
                    Pair(accumulator.first+pdf, accumulator.second+pages)
                }
                else {
                    throw java.lang.IllegalArgumentException(connection.contentType)
                }
            }
            else if (chapter.file?.endsWith(".md") == true) {
                val output = chapter.file?.replace(Regex("\\.md"), ".pdf")
                val html = processor.process(source.loader.source(chapter.file!!).reader, source, index+1)
                val (pdf, pageCount) = renderer.render(html, accumulator.second)
                val path = FileSystems.getDefault().getPath("target", output)
                Files.createDirectories(path.parent)
                Files.write(path, pdf)
                Pair(accumulator.first+pdf, accumulator.second+pageCount)
            }
            else if (chapter.file?.endsWith(".pdf") == true) {
                val pageCount = source.loader.source(chapter.file!!).inputStream.use {
                    PdfDocument(PdfReader(it)).numberOfPages
                }
                val html = processor.blank(pageCount + pageCount.and(1), source)
                val transform: DoubleArray = (chapter.transformation?:arrayOf(1.0, 0.0, 0.0, 1.0)).toDoubleArray()
                val transformation = AffineTransform(transform)
                val (pdf, pages) =  renderer.render(html, source.loader.source(chapter.file!!).inputStream, transformation, accumulator.second)
                val path = FileSystems.getDefault().getPath("target", chapter.file)
                Files.createDirectories(path.parent)
                Files.write(path, pdf)
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
