package my.bookmaker.processor

import my.bookmaker.source.ClassLoaderSource
import my.bookmaker.source.Source
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.StringReader

class ProcessorTest
{
    val metadata: Source = ClassLoaderSource(this::class.java.classLoader, "metadata.yml")

    @Test
    fun test() {
        val processor = Processor()
        val reader = StringReader("Hello *World*!")
        val result: String = processor.process(reader, metadata)
        val expected: String =
        """
          <html>
            <head>
              <style>
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
              </style>
            </head>
            <body>
              <p>Hello <em>World</em>!</p>
              <div style="page-break-before: always;">&#0160;</div>
            </body>
          </html>
        """.trimIndent()
        assertEquals(expected, result)
    }

    @Test
    fun indented()
    {
        val input = "Line 1\nLine 2\nLine 3"
        val result = Processor().run{input.indented(2)}
        val expected = "Line 1\n  Line 2\n  Line 3"
        assertEquals(expected, result)
    }
}
