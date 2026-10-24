//                                                                            //
// My Bookmaker - Markdown-based creation of printed books                    //
// Copyright (C) 2023 - 2026 Mirko Raner                                      //
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

import my.bookmaker.metadata.Metadata
import my.bookmaker.source.FileSystemLoader
import my.bookmaker.source.FileSystemSource
import my.bookmaker.source.Source
import my.bookmaker.source.StringSource
import my.bookmaker.utilities.DefaultTitleProcessor
import my.bookmaker.utilities.TitleProcessor
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugin.MojoExecutionException
import org.apache.maven.plugin.MojoFailureException
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.velocity.Template
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.VelocityEngine
import java.io.ByteArrayOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributes
import java.util.function.BiPredicate
import java.util.stream.Collectors.toMap
import java.util.stream.Stream
import kotlin.io.path.createDirectories
import kotlin.io.path.createTempFile
import kotlin.io.path.extension
import kotlin.streams.toList


/**
 * {@link Book} is a (poorly named) Maven Mojo for compiling a book based on a book.yml description.
 *
 * @author Mirko Raner
 */
@Mojo(name="book", defaultPhase=LifecyclePhase.COMPILE, requiresProject=false)
class Book: AbstractMojo {

    private val cwd: Path

    /**
     * Creates a new {@link Book} mojo. This constructor will be invoked by the Maven Plexus framework.
     */
    @Suppress("unused")
    constructor(): this(Paths.get("."))

    constructor(cwd: Path) {
        this.cwd = cwd
    }

    companion object {
        private const val DEFAULT_YML = "book.yml"
    }

    @Throws(MojoExecutionException::class, MojoFailureException::class)
    override fun execute() {
        val target: Path = cwd.resolve("target").apply {
            toFile().deleteRecursively()
            createDirectories()
        }
        if (Files.exists(cwd.resolve(DEFAULT_YML))) {
            val source: Source = FileSystemSource(cwd, DEFAULT_YML)
            log.info("Loading $DEFAULT_YML from ${cwd.toAbsolutePath()}")
            Metadata(MavenLogger(log)).make(source)
        }
        else {

            // There is no YAML file describing a book.
            // Collect all Markdown and CSS files and process each Markdown file individually.
            // To support variable expansion, each Markdown files gets passed a custom CSS file.
            //
            val files: Stream<Path> = Files.find(cwd, Integer.MAX_VALUE, {_, it -> it.isRegularFile})
            val urls: Map<String, String> = files.collect(toMap({path -> cwd.relativize(path).toString()}, {path -> path.toAbsolutePath().toUri().toURL().toString()}))
            val mdFiles: Stream<Path> = Files.find(cwd, 1, filesWithExtension("md"))
            val sources: Stream<Source> = mdFiles.map {

                // Create source-specific CSS (for file-specific variables):
                //
                val title = it.toFile().nameWithoutExtension
                val variables: Map<String, Any> = variables(title, urls)
                val cssFiles: List<Path> = Files.find(cwd, 1, filesWithExtension("css")).sorted(byName()).toList()
                val tempPath: Path = cwd.relativize(createTempFile(target, "style-", ".css"))
                val cssFile: Path = tempPath.apply {
                    val cssConcatenate: (Unit, Path) -> Unit = {
                        _, path: Path -> cwd.resolve(this).toFile().appendBytes(interpolate(path, variables))
                    }
                    cssFiles.fold(Unit, cssConcatenate)
                }

                // Create "Book" description for individual MD file:
                //
                val yaml = """
                    title: $title
                    style: $cssFile
                    trim: 8.5 x 11 in
                    manuscript:
                      chapters:
                        - file: ${cwd.relativize(it)}
                """.trimIndent()

                // Return an in-memory StringSource for the Book:
                //
                StringSource(yaml, FileSystemLoader(cwd))
            }
            val metadata = Metadata(MavenLogger(log))
            for (source in sources) {
                metadata.make(source)
            }
        }
    }

    private fun interpolate(template: Path, variables: Map<String, Any>): ByteArray {
        val engine = VelocityEngine()
        val template: Template? = engine.getTemplate(pathToString(template))
        val context = VelocityContext().apply {variables.forEach(this::put)}
        return ByteArrayOutputStream().apply {
            OutputStreamWriter(this, UTF_8).use { template!!.merge(context, it) }
        }.toByteArray()
    }

    private fun pathToString(path: Path): String {
        return if (path.startsWith(".")) {
            path.toString()
        }
        else {
            Paths.get(".").resolve(path).toString()
        }
    }

    private fun variables(title: String, urls: Map<String, String>): Map<String, Any> {
        val titleProcessor: TitleProcessor = DefaultTitleProcessor()
        val (id, name) = titleProcessor.documentInfo(title)
        return hashMapOf(
            "id" to id,
            "name" to name,
            "pages" to 0,
            "url" to urls
        ) as Map<String, Any>
    }

    private fun filesWithExtension(extension: String): BiPredicate<Path, BasicFileAttributes> {
        return {path, attributes -> attributes.isRegularFile && path.extension == extension }
    }

    private fun byName(): Comparator<Path> = { o1, o2 -> o1.toString().compareTo(o2.toString()) }
}