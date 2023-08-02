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
package my.bookmaker.processor

import my.bookmaker.source.Source
import my.bookmaker.styler.Styler
import org.commonmark.node.Node
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import java.io.Reader
import java.io.StringReader

class Processor
{
    fun process(manuscript: Reader, metadata: Source): String
    {
        val styler = Styler()
        val parser: Parser = Parser.builder().build()
        val document: Node = parser.parseReader(manuscript)
        val renderer: HtmlRenderer = HtmlRenderer.builder().build()
        return """
            <html>
              <head>
                <style>
                  ${styler.style(metadata).indented(18)}
                </style>
              </head>
              <body>
                ${renderer.render(document).indented(16).trimEnd()}
                <div style="page-break-before: always;">&#0160;</div>
              </body>
            </html>
        """.trimIndent()
    }


    fun blank(pages: Int, metadata: Source): String
    {
        val blank = """<div style="page-break-after: always;">&#0160;</div>"""
        val reader: Reader = StringReader(blank.repeat(pages-1))
        return process(reader, metadata)
    }

    fun String.indented(indentation: Int): String
    {
        val lines: List<String> = lines()
        val first: String = lines.first().trim()
        val tail: List<String> = lines.drop(1).map{it.prependIndent(" ".repeat(indentation))}
        return (listOf(first) + tail).joinToString("\n")
    }
}
