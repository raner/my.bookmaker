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
package my.bookmaker.toc

import my.bookmaker.renderer.DrawingListener
import org.w3c.dom.Element
import org.xhtmlrenderer.render.InlineText
import org.xhtmlrenderer.render.RenderingContext

/**
 * ...
 * This is a mutable, stateful listener; replace with an observable.
 */
class TableOfContents: DrawingListener {

    private var tagState: String? = null

    private val headerTags = hashSetOf("h1", "h2", "h3", "h4", "h5", "h6")

    val toc: MutableList<Triple<String, Int, Int>> = arrayListOf()

    override fun drawText(context: RenderingContext, text: InlineText, pageOffset: Int) {
        val tag = text.parent.element?.tagName?:(text.textNode?.parentNode as? Element)?.tagName
        if (tag in headerTags) {
            if (tagState != tag) {
                // New tag, add new ToC entry:
                val level: Int = tag!!.toCharArray()[1]-'1'
                val page: Int = context.pageNo + pageOffset
                toc.add(Triple(text.substring, page, level))
                tagState = tag
            }
            else {
                // Tag state is unchanged, append to the last ToC entry
                val current: Triple<String, Int, Int> = toc.removeLast()
                toc.add(Triple(current.first + text.substring, current.second, current.third))
            }
        }
        else {
            tagState = null
        }
    }

    fun addEntry(title: String, page: Int, level: Int = 0) {
        toc.add(Triple(title, page, level))
        tagState = null
    }

    /**
     * Returns an HTML representation of a styled table of contents.
     */
    fun styledToC(levels: Int = 2): String {

        val html = StringBuilder("<div class=\"toc-title></div>\n")
        var level = -1
        for (entry in toc) {
            val difference = entry.third - level
            level = entry.third
            if (difference > 0) {
                html.append("""
                    <ol class="toc" role="list">
                    
                """.trimIndent().indented(level*2+2))
            }
            else if (difference < 0) {
                html.append("""
                    </ol>
                    
                """.trimIndent().indented(level*2+2))
            }
            html.append("""
                <li>
                  <span class="toc-entry">${entry.first}</span>
                  <span class="toc-page">${entry.second}</span>
                </li>
                
            """.trimIndent().indented(level*2+2))
        }
        val trimmed = html.trimEnd{it == ' '}
        html.clear().append(trimmed)
        while (level-- >= 0) {
            html.append("</ol>\n")
        }
        return html.toString()
    }

    // TODO: copied from Processor
    fun String.indented(indentation: Int): String
    {
        val lines: List<String> = lines()
        val first: String = lines.first().trim()
        val tail: List<String> = lines.drop(1).map{it.prependIndent(" ".repeat(indentation))}
        return (listOf(first) + tail).joinToString("\n")
    }
}