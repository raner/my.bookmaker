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

import my.bookmaker.metadata.Book
import my.bookmaker.metadata.Metadata
import my.bookmaker.source.Source
import com.google.common.io.CharStreams

/**
 * The {@code Styler} loads a book's CSS style and augments it with certain variable
 * parts that typically cannot be hard-coded. This includes the page size (which is
 * defined separately in the book's YAML file), as well as section counters (which
 * vary according to how many previous sections were already processed).
 * Three different section counters are supported: {@code section}, {@code chapter},
 * and {@code h1}. This allows for custom CSS styling using counter names likes
 * {@code section}/{@code subsection}/{@code subsubsection},
 * {@code chapter}/{@code paragraph}, or {@code h1}/{@code h2}/{@code h3}/{@code h4}.
 * There is no default section numbering unless configured via CSS.
 *
 * @author Mirko Raner
 */
class Styler
{
    val metadata: Metadata = Metadata()

    /**
     * Provides CSS styling for a book.
     *
     * @param source the source from which to load the book's YAML definition
     * @param section the initial section number (defaults to 1)
     * @return the preprocessed CSS for the book
     **/
    fun style(source: Source, section: Int = 1): String
    {
        val book: Book = metadata.book(source)
        val styleSource: Source = source.loader.source(book.style)
        val style: String = CharStreams.toString(styleSource.reader)
        val (width, _, height, unit) = book.trim.split(" ")
        val resetCounters = """
            body
            {
              counter-reset: section ${section-1} chapter ${section-1} h1 ${section-1};
            }""".trimIndent()
        return "$resetCounters\n@page {size: $width$unit $height$unit;}\n$style".trimEnd()
    }
}