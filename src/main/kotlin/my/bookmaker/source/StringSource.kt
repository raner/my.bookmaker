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
package my.bookmaker.source

import java.io.InputStream
import java.net.URL
import kotlin.TODO
import kotlin.text.Charsets.UTF_8

/**
 * A {@link StringSource} is a {@link Source} reads content from a predefined string.
 *
 * @author Mirko Raner
 */
class StringSource(val string: String, override val loader: Loader): Source {
    override val url: URL get() = TODO("Not yet implemented")
    override val path: String get() = TODO("Not yet implemented")
    override val inputStream: InputStream get() = string.byteInputStream(UTF_8)
}