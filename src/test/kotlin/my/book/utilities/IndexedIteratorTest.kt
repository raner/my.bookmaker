package my.book.utilities

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