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

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals

class DefaultTitleProcessorTest
{
    val processor: TitleProcessor = DefaultTitleProcessor()

    @Test
    fun titleWithSubtitle() {
        val title = "The Strange Case of Dr. Jekyll and Mr. Hyde - A Gothic Romance"
        val result = processor.processTitle(title)
        assertEquals("The Strange Case of Dr. Jekyll and Mr. Hyde", result)
    }

    @Test
    fun titleWithoutSubtitle() {
        val title = "How to Win Friends and Influence People"
        val result = processor.processTitle(title)
        assertEquals("How to Win Friends and Influence People", result)
    }

    @Test
    fun documentWithoutId() {
        val title = "Standard Operating Procedure"
        val (id, name) = processor.documentInfo(title)
        assertEquals("", id)
        assertEquals("Standard Operating Procedure", name)
    }

    @Test
    fun documentWithPurelyNumericId() {
        val title = "00001 Standard Operating Procedure"
        val (id, name) = processor.documentInfo(title)
        assertEquals("00001", id)
        assertEquals("Standard Operating Procedure", name)
    }

    @Test
    fun documentWithAlphaNumericId() {
        val title = "DOC00001 Standard Operating Procedure"
        val (id, name) = processor.documentInfo(title)
        assertEquals("DOC00001", id)
        assertEquals("Standard Operating Procedure", name)
    }

    @Test
    fun documentWithHyphenatedId() {
        val title = "SOP-001 Standard Operating Procedure"
        val (id, name) = processor.documentInfo(title)
        assertEquals("SOP-001", id)
        assertEquals("Standard Operating Procedure", name)
    }

    @Test
    fun documentWithAnotherHyphenatedId() {
        val title = "DOC-002 Short Summary"
        val (id, name) = processor.documentInfo(title)
        assertEquals("DOC-002", id)
        assertEquals("Short Summary", name)
    }

    @Test
    fun documentWithDoubleHyphenatedId() {
        val title = "PTR-2026-1-5 Penetration Test Report"
        val (id, name) = processor.documentInfo(title)
        assertEquals("PTR-2026-1-5", id)
        assertEquals("Penetration Test Report", name)
    }
}