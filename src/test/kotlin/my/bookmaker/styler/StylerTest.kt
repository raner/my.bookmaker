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
package my.bookmaker.styler

import my.bookmaker.source.ClassLoaderSource
import my.bookmaker.source.Source
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class StylerTest
{
    @Test
    fun test()
    {
        val source: Source = ClassLoaderSource(this::class.java.classLoader, "metadata.yml")
        val styler = Styler()
        val css: String = styler.style(source)
        val expected: String = """
        @page {size: 7.5in 9.25in;}
        @page
        {
          margin-top: 0.7in;
          margin-bottom: 0.6in;
        }
        @page :left
        {
          margin-left: 0.6in;
          margin-right: 0.9in;
          @left-bottom
          {
            content: '|\a|\a|\a|\a|\a|\a|\a|\a' counter(page) ' |';
            text-align: right;
            white-space: pre;
            line-height: 65%;
          }
        }
        @page :right
        {
          margin-left: 0.9in;
          margin-right: 0.6in;
          @right-bottom
          {
            content: '|\a|\a|\a|\a|\a|\a|\a|\a| ' counter(page);
            text-align: left;
            white-space: pre;
            line-height: 65%;
          }
        }
        """.trimIndent()
        assertEquals(expected, css)
    }
}
