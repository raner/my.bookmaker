package my.book.styler

import my.book.source.ClassLoaderSource
import my.book.source.Source
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
