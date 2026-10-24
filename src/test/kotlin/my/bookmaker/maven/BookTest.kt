//                                                                            //
// My Bookmaker - Markdown-based creation of printed books                    //
// Copyright (C) 2026 Mirko Raner                                             //
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
package my.bookmaker.maven

import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.parser.ImageRenderInfo
import com.itextpdf.text.pdf.parser.MultiFilteredRenderListener
import com.itextpdf.text.pdf.parser.PdfImageObject
import com.itextpdf.text.pdf.parser.PdfReaderContentParser
import com.itextpdf.text.pdf.parser.PdfTextExtractor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.FileInputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class BookTest
{
    @Test
    fun multipleSources()
    {
        val multiple: Path = Paths.get("src/test/resources/multiple")
        val book = Book(multiple)
        Files.deleteIfExists(multiple.resolve("target/Projo Introduction.pdf"))
        Files.deleteIfExists(multiple.resolve("target/Projo Short Summary.pdf"))
        book.execute()
        assertTrue(Files.exists(multiple.resolve("target/Projo Introduction.pdf")))
        assertTrue(Files.exists(multiple.resolve("target/Projo Short Summary.pdf")))
    }

    @Test
    fun variableDocumentName()
    {
        val path: Path = Paths.get("src/test/resources/variables")
        val book = Book(path)
        Files.deleteIfExists(path.resolve("target/DOC-001 Projo Introduction.pdf"))
        Files.deleteIfExists(path.resolve("target/DOC-002 Projo Short Summary.pdf"))
        book.execute()
        val pdfReader = PdfReader(FileInputStream("target/DOC-001 Projo Introduction.pdf"))
        val line: List<String> = PdfTextExtractor.getTextFromPage(pdfReader, 1).lines()
        assertEquals("Projo Introduction", line[0])
    }

    @Test
    fun variableDocumentId()
    {
        val path: Path = Paths.get("src/test/resources/variables")
        val book = Book(path)
        Files.deleteIfExists(path.resolve("target/DOC-001 Projo Introduction.pdf"))
        Files.deleteIfExists(path.resolve("target/DOC-002 Projo Short Summary.pdf"))
        book.execute()
        val pdfReader = PdfReader(FileInputStream("target/DOC-002 Projo Short Summary.pdf"))
        val line: List<String> = PdfTextExtractor.getTextFromPage(pdfReader, 1).lines()
        assertEquals("Document DOC-002", line[1])
    }

    @Test
    fun urlImageInclusion()
    {
        val path: Path = Paths.get("src/test/resources/variables")
        val book = Book(path)
        Files.deleteIfExists(path.resolve("target/DOC-001 Projo Introduction.pdf"))
        Files.deleteIfExists(path.resolve("target/DOC-002 Projo Short Summary.pdf"))
        book.execute()
        val pdfReader = PdfReader(FileInputStream("target/DOC-002 Projo Short Summary.pdf"))
        val parser = PdfReaderContentParser(pdfReader)
        val listener = object: MultiFilteredRenderListener() {
            var image: PdfImageObject? = null
            override fun renderImage(renderInfo: ImageRenderInfo) {
                image = renderInfo.image
            }
        }
        val image: PdfImageObject? = listener.run {
            parser.processContent(1, this).image
        }
        assertEquals(472, image?.bufferedImage?.width)
    }
}
