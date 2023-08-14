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
        for (entry in toc.filter{it.third < levels}) {
            val difference = entry.third - level
            level = entry.third
            if (difference > 0) {
                val tocClass = if (level == 0) """class="toc" """ else ""
                html.append(indent(level*4) + """<ol ${tocClass}role="list">""" + "\n")
            }
            else if (difference < 0) {
                for (close in 1..-difference) {
                    html
                        .append(indent((level-difference-close+1)*4+2) + "</li>\n")
                        .append(indent((level-difference-close+1)*4) + "</ol>\n")
                }
            }
            if (difference <= 0) {
                html.append(indent(level*4+2) + "</li>\n")
            }
            html
                .append(indent(level*4+2) + "<li>\n")
                .append(indent(level*4+4) + """<span class="toc-entry">${entry.first}</span>""" + "\n")
                .append(indent(level*4+4) + """<span class="toc-page">${entry.second}</span>""" + "\n")
        }
        val trimmed = html.trimEnd{it == ' '}
        html.clear().append(trimmed) // TODO: use replace
        while (level-- >= 0) {
            html
                .append(indent(level*4+6) + "</li>\n")
                .append(indent(level*4+4) + "</ol>\n")
        }
        return html.toString()
    }

    private fun indent(indentation: Int): String {
        return " ".repeat(indentation)
    }
}