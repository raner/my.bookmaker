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

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TableOfContentsTest {

    @Test
    fun oneLevelTableOfContents() {
        val toc = TableOfContents()
        toc.addEntry("Introduction", 1)
        toc.addEntry("Discussion", 100)
        toc.addEntry("Afterword", 1000)
        val result = toc.styledToC()
        val expected = """
            <div class="toc-title></div>
            <ol class="toc" role="list">
              <li>
                <span class="toc-entry">Introduction</span>
                <span class="toc-page">1</span>
              </li>
              <li>
                <span class="toc-entry">Discussion</span>
                <span class="toc-page">100</span>
              </li>
              <li>
                <span class="toc-entry">Afterword</span>
                <span class="toc-page">1000</span>
              </li>
            </ol>
            
        """.trimIndent()
        assertEquals(expected, result)
    }

    @Test
    fun multiLevelTableOfContents() {
        val toc = TableOfContents()
        toc.addEntry("Introduction", 1)
        toc.addEntry("Fundamentals", 1, 1)
        toc.addEntry("Terminology", 10, 1)
        toc.addEntry("Discussion", 100)
        toc.addEntry("Rumsfeld Matrix", 100, 1)
        toc.addEntry("Known Knowns", 100, 2)
        toc.addEntry("Known Unknowns", 200, 2)
        toc.addEntry("Unknown Knowns", 300, 2)
        toc.addEntry("Unknown Unknowns", 400, 2)
        toc.addEntry("Hallin's Spheres", 500, 1)
        toc.addEntry("Consensus", 600, 2)
        toc.addEntry("Controversy", 700, 2)
        toc.addEntry("Deviance", 800, 2)
        toc.addEntry("Afterword", 1000)
        val expected = """
            <div class="toc-title></div>
            <ol class="toc" role="list">
              <li>
                <span class="toc-entry">Introduction</span>
                <span class="toc-page">1</span>
                <ol role="list">
                  <li>
                    <span class="toc-entry">Fundamentals</span>
                    <span class="toc-page">1</span>
                  </li>
                  <li>
                    <span class="toc-entry">Terminology</span>
                    <span class="toc-page">10</span>
                  </li>
                </ol>
              </li>
              <li>
                <span class="toc-entry">Discussion</span>
                <span class="toc-page">100</span>
                <ol role="list">
                  <li>
                    <span class="toc-entry">Rumsfeld Matrix</span>
                    <span class="toc-page">100</span>
                    <ol role="list">
                      <li>
                        <span class="toc-entry">Known Knowns</span>
                        <span class="toc-page">100</span>
                      </li>
                      <li>
                        <span class="toc-entry">Known Unknowns</span>
                        <span class="toc-page">200</span>
                      </li>
                      <li>
                        <span class="toc-entry">Unknown Knowns</span>
                        <span class="toc-page">300</span>
                      </li>
                      <li>
                        <span class="toc-entry">Unknown Unknowns</span>
                        <span class="toc-page">400</span>
                      </li>
                    </ol>
                  </li>
                  <li>
                    <span class="toc-entry">Hallin's Spheres</span>
                    <span class="toc-page">500</span>
                    <ol role="list">
                      <li>
                        <span class="toc-entry">Consensus</span>
                        <span class="toc-page">600</span>
                      </li>
                      <li>
                        <span class="toc-entry">Controversy</span>
                        <span class="toc-page">700</span>
                      </li>
                      <li>
                        <span class="toc-entry">Deviance</span>
                        <span class="toc-page">800</span>
                      </li>
                    </ol>
                  </li>
                </ol>
              </li>
              <li>
                <span class="toc-entry">Afterword</span>
                <span class="toc-page">1000</span>
              </li>
            </ol>
            
        """.trimIndent()
        val result = toc.styledToC(3)
        assertEquals(expected, result)
    }

    @Test
    fun multiLevelTableOfContentsWithLimit() {
        val toc = TableOfContents()
        toc.addEntry("Introduction", 1)
        toc.addEntry("Fundamentals", 1, 1)
        toc.addEntry("Terminology", 10, 1)
        toc.addEntry("Discussion", 100)
        toc.addEntry("Rumsfeld Matrix", 100, 1)
        toc.addEntry("Known Knowns", 100, 2)
        toc.addEntry("Known Unknowns", 200, 2)
        toc.addEntry("Unknown Knowns", 300, 2)
        toc.addEntry("Unknown Unknowns", 400, 2)
        toc.addEntry("Hallin's Spheres", 500, 1)
        toc.addEntry("Consensus", 600, 2)
        toc.addEntry("Controversy", 700, 2)
        toc.addEntry("Deviance", 800, 2)
        toc.addEntry("Afterword", 1000)
        val expected = """
            <div class="toc-title></div>
            <ol class="toc" role="list">
              <li>
                <span class="toc-entry">Introduction</span>
                <span class="toc-page">1</span>
                <ol role="list">
                  <li>
                    <span class="toc-entry">Fundamentals</span>
                    <span class="toc-page">1</span>
                  </li>
                  <li>
                    <span class="toc-entry">Terminology</span>
                    <span class="toc-page">10</span>
                  </li>
                </ol>
              </li>
              <li>
                <span class="toc-entry">Discussion</span>
                <span class="toc-page">100</span>
                <ol role="list">
                  <li>
                    <span class="toc-entry">Rumsfeld Matrix</span>
                    <span class="toc-page">100</span>
                  </li>
                  <li>
                    <span class="toc-entry">Hallin's Spheres</span>
                    <span class="toc-page">500</span>
                  </li>
                </ol>
              </li>
              <li>
                <span class="toc-entry">Afterword</span>
                <span class="toc-page">1000</span>
              </li>
            </ol>
            
        """.trimIndent()
        val result = toc.styledToC(2)
        assertEquals(expected, result)
    }
}