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

    private val headerTags = hashSetOf<String>("h1", "h2", "h3", "h4", "h5", "h6")

    val toc: MutableList<Pair<String, String>> = arrayListOf()

    override fun drawText(context: RenderingContext, text: InlineText, pageOffset: Int) {
        val tag = text.parent.element?.tagName?:(text.textNode?.parentNode as? Element)?.tagName
        if (tag in headerTags) {
            if (tagState != tag) {
                // New tag, add new ToC entry:
                val page: String = (context.pageNo + pageOffset).toString()
                toc.add(Pair(page, text.substring))
            }
            else {
                // Tag state is unchanged, append to the last ToC entry
                val current: Pair<String, String> = toc.removeLast()
                toc.add(Pair(current.first, current.second + text.substring))
            }
        }
        else {
            tagState = null
        }
    }
}