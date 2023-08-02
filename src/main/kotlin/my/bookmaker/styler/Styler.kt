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

class Styler
{
    val metadata: Metadata = Metadata()

    /**
     * Provides CSS styling for a book.
     **/
    fun style(source: Source): String
    {
        val book: Book = metadata.book(source)
        val styleSource: Source = source.loader.source(book.style)
        val style: String = CharStreams.toString(styleSource.reader)
        val (width, _, height, unit) = book.trim.split(" ")
        return "@page {size: $width$unit $height$unit;}\n$style".trimEnd()
    }
}