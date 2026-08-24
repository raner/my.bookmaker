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
import my.bookmaker.source.FileSystemSource
import my.bookmaker.source.Source
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugin.MojoExecutionException
import org.apache.maven.plugin.MojoFailureException
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Maven Mojo for compiling a book based on a book.yml description.
 *
 * @author Mirko Raner
 */
@Mojo(name="book", defaultPhase=LifecyclePhase.COMPILE, requiresProject=false)
class Book: AbstractMojo() {
    @Throws(MojoExecutionException::class, MojoFailureException::class)
    override fun execute() {
        val cwd: Path = Paths.get(".")
        val source: Source = FileSystemSource(cwd, "book.yml")
        log.info("Loading book.yml from ${cwd.toAbsolutePath()}")
        Files.createDirectories(cwd.resolve("target"))
        Metadata(MavenLogger(log)).make(source)
    }
}