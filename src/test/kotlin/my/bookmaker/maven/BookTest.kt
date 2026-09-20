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
package my.bookmaker.maven

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class BookTest
{
    @Test
    fun multipleSources()
    {
        val multiple: Path = Paths.get("src/test/resources/multiple")
        val book = Book(multiple)
        Files.deleteIfExists(multiple.resolve("target/Projo Introduction.pdf"))
        Files.deleteIfExists(multiple.resolve("target/Projo Short Summary.pdf"))
        book.execute()
        assertTrue(Files.exists(multiple.resolve("target/Projo Introduction.pdf")))
        assertTrue(Files.exists(multiple.resolve("target/Projo Short Summary.pdf")))
    }
}
