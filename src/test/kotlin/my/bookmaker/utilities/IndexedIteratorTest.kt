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
package my.bookmaker.utilities

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals

class IndexedIteratorTest
{
    @Test
    fun testIndexedIterator()
    {
        val list: List<String> = listOf("Hello", " ", "World", "!")
        val iterator: IndexedIterator<List<String>, String> = IndexedIterator(list, {it.size}, {it, index -> it[index]})
        val buffer = StringBuffer()
        iterator.forEach{buffer.append(it)}
        assertEquals("Hello World!", buffer.toString())
    }
}