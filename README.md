## My Bookmaker &nbsp; [![Build Status](https://github.com/raner/my.bookmaker/actions/workflows/build.yml/badge.svg?branch=main)](https://github.com/raner/my.bookmaker/actions?query=branch:main) [![Maven Central](https://img.shields.io/maven-central/v/my.bookmaker/make.svg)](https://oss.sonatype.org/content/repositories/releases/my/bookmaker/make/)
**My Bookmaker** is a Maven plugin for producing formatted PDFs from Markdown and other sources.
To generate some PDFs, simply use the following command in a directory that contains Markdown files:
```
mvn my.bookmaker:make:book
```
Despite being a Maven plugin, My Bookmaker does not require a `pom.xml` file.

Here is how it works in a nutshell:
* Configuration happens either via a YAML file or by convention:
  * If a file called `book.yml` exists then the configuration will be sourced from that file.
    Typically, a single final output PDF is produced.
  * If no YAML configuration file exists, all Markdown files in the current directory will
    be converted to individual PDFs. CSS files will be collected and applied together.
* Markdown is converted into HTML using the CommonMark Java library (see [Processor](https://github.com/raner/my.bookmaker/blob/main/src/main/kotlin/my/bookmaker/processor/Processor.kt)):
  * CSS content is pasted into the `<head><style>` section of the final HTML document.
  * The [Styler](https://github.com/raner/my.bookmaker/blob/main/src/main/kotlin/my/bookmaker/styler/Styler.kt)
    will also append some additional boilerplate CSS for setting the page size and managing
    section counters.
* Styled HTML is converted to PDF using the [Flying Saucer](https://github.com/flyingsaucerproject/flyingsaucer)
  XHTML/CSS renderer with the (discontinued) [flying-saucer-pdf-itext5](https://mvnrepository.com/artifact/org.xhtmlrenderer/flying-saucer-pdf-itext5)
  back-end (based on [iTextPDF 5.5](https://mvnrepository.com/artifact/com.itextpdf/itextpdf/5.5.13.6);
  see [Renderer](https://github.com/raner/my.bookmaker/blob/main/src/main/kotlin/my/bookmaker/renderer/Renderer.kt)).