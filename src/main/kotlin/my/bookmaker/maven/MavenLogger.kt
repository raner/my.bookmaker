package my.bookmaker.maven

import org.apache.maven.plugin.logging.Log
import java.util.logging.Logger

class MavenLogger(private val log: Log): Logger("(Maven)", null) {

    override fun info(message: String?) {
        log.info(message)
    }

    override fun warning(message: String?) {
        log.warn(message)
    }

    override fun severe(message: String?) {
        log.error(message)
    }
}