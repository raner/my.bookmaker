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
@Mojo(name="compile", defaultPhase=LifecyclePhase.COMPILE, requiresProject=false)
class Compile: AbstractMojo() {
    @Throws(MojoExecutionException::class, MojoFailureException::class)
    override fun execute() {
        val cwd: Path = Paths.get(".")
        val source: Source = FileSystemSource(cwd, "book.yml")
        log.info("Loading book.yml from ${cwd.toAbsolutePath()}")
        Files.createDirectories(cwd.resolve("target"))
        Metadata().make(source)
    }
}