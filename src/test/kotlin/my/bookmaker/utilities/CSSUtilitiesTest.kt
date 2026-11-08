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

import my.bookmaker.utilities.CSSUtilities.hasClassCondition
import my.bookmaker.utilities.CSSUtilities.hasPseudoPage
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.xhtmlrenderer.css.parser.CSSErrorHandler
import org.xhtmlrenderer.css.sheet.PageRule
import org.xhtmlrenderer.css.sheet.Ruleset
import org.xhtmlrenderer.css.sheet.Stylesheet
import java.nio.file.Paths

class CSSUtilitiesTest
{
    val utilities = CSSUtilities
    val errorHandler: CSSErrorHandler = { uri, message -> println("$uri: $message")}

    @Test
    fun testStylesheetHasLeftPage() {
        val path = Paths.get("src/test/resources/css/style.css")
        val stylesheet: Stylesheet = utilities.stylesheet(path, errorHandler)
        val hasLeftPage: Boolean = stylesheet.hasPseudoPage("left")
        assertTrue(hasLeftPage)
    }

    @Test
    fun testListOfPageRulesHasLeftPage() {
        val path = Paths.get("src/test/resources/css/style.css")
        val stylesheet: Stylesheet = utilities.stylesheet(path, errorHandler)
        val pageRules: List<PageRule> = stylesheet.contents.filterIsInstance<PageRule>()
        val hasLeftPage: Boolean = pageRules.hasPseudoPage("left")
        assertTrue(hasLeftPage)
    }

    @Test
    fun testListOfPageRulesDoesNotHaveRightPage() {
        val path = Paths.get("src/test/resources/css/style.css")
        val stylesheet: Stylesheet = utilities.stylesheet(path, errorHandler)
        val pageRules: List<PageRule> = stylesheet.contents.filterIsInstance<PageRule>()
        val hasRightPage: Boolean = pageRules.hasPseudoPage("right")
        assertFalse(hasRightPage)
    }

    @Test
    fun testStylesheetHasClassConditionToC() {
        val path = Paths.get("src/test/resources/css/style.css")
        val stylesheet: Stylesheet = utilities.stylesheet(path, errorHandler)
        val hasClass: Boolean = stylesheet.hasClassCondition("toc")
        assertTrue(hasClass)
    }

    @Test
    fun testListOfRulesetsHasClassConditionToC() {
        val path = Paths.get("src/test/resources/css/style.css")
        val stylesheet: Stylesheet = utilities.stylesheet(path, errorHandler)
        val rulesets: List<Ruleset> = stylesheet.contents.filterIsInstance<Ruleset>()
        val hasClass: Boolean = rulesets.hasClassCondition("toc")
        assertTrue(hasClass)
    }

    @Test
    fun testListOfRulesetsDoesNotHaveClassConditionTic() {
        val path = Paths.get("src/test/resources/css/style.css")
        val stylesheet: Stylesheet = utilities.stylesheet(path, errorHandler)
        val rulesets: List<Ruleset> = stylesheet.contents.filterIsInstance<Ruleset>()
        val hasClass: Boolean = rulesets.hasClassCondition("tic")
        assertFalse(hasClass)
    }
}