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
package my.bookmaker.utilities

import org.xhtmlrenderer.css.extend.AttributeResolver
import org.xhtmlrenderer.css.newmatch.Selector
import org.xhtmlrenderer.css.parser.CSSErrorHandler
import org.xhtmlrenderer.css.parser.CSSParser
import org.xhtmlrenderer.css.sheet.PageRule
import org.xhtmlrenderer.css.sheet.Ruleset
import org.xhtmlrenderer.css.sheet.Stylesheet
import pro.projo.Projo.creates
import pro.projo.annotations.Delegate
import java.io.Reader
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import pro.projo.singles.Factory

/**
 * The {@link CSSUtilities} object provides utility functions for analyzing CSS content.
 * It provides methods for finding specific rules in stylesheets.
 * To avoid bringing in yet another CSS library, the code is based on the CSS parser
 * provided by Flying Saucer, which is geared towards applying CSS to HTML, but not so
 * much towards analyzing and querying CSS stylesheets. For this reason, some of the
 * methods are slightly awkward and involve synthetic attribute resolvers.
 *
 * @author Mirko Raner
 */
object CSSUtilities {

    interface MapBasedAttributeResolver: AttributeResolver {

        companion object {
            val FACTORY: Factory<MapBasedAttributeResolver, AttributeResolver> =
                creates(MapBasedAttributeResolver::class.java).with(MapBasedAttributeResolver::delegated)
        }

        @Delegate
        fun delegated(): AttributeResolver

        override fun getClass(element: Any?): String? = (element as Map<*, *>)["class"].toString()
    }

    fun stylesheet(cssPath: Path, errorHandler: CSSErrorHandler): Stylesheet {
        val parser = CSSParser(errorHandler)
        val uri: URI = cssPath.toUri()
        val reader: Reader = Files.newBufferedReader(cssPath)
        return parser.parseStylesheet(uri.toString(), 0, reader)
    }

    fun Stylesheet.hasPseudoPage(pseudoPage: String?): Boolean {
        val pageRules: List<PageRule> = contents.filterIsInstance<PageRule>()
        return pageRules.hasPseudoPage(pseudoPage)
    }

    fun List<PageRule>.hasPseudoPage(pseudoPage: String?): Boolean {
        return any{it.pseudoPage == pseudoPage}
    }

    fun Stylesheet.hasClassCondition(className: String): Boolean {
        val rulesets: List<Ruleset> = contents.filterIsInstance<Ruleset>()
        return rulesets.hasClassCondition(className)
    }

    fun List<Ruleset>.hasClassCondition(className: String): Boolean {
        return any {
            it.fsSelectors.filterIsInstance<Selector>().any { selector ->
                val element: Map<String, String> = hashMapOf("class" to className)
                val attributeResolver: AttributeResolver = MapBasedAttributeResolver.FACTORY.create(null)
                return selector.matches(element, attributeResolver, null)
            }
        }
    }
}