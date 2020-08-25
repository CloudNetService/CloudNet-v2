package eu.cloudnetservice.cloudnet.v2.logging

import java.io.IOException
import java.io.PrintStream
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.util.logging.*


class CloudLogger : Logger("CloudLogger",null) {

    var debugging: Boolean = false

    init {
        level = Level.ALL
        //getLogger("org.jline").parent = this
        val logsFolder = Paths.get("local","logs")
        Files.createDirectories(logsFolder)
        val fh = FileHandler("local/logs/latest.%g.log", 80000, 10, true)
        fh.formatter = LoggingFormatter()
        fh.level = Level.ALL
        fh.encoding = StandardCharsets.UTF_8.name()
        addHandler(fh)
    }

    /**
     * This posts a new debug message, if [.debugging] is true.
     *
     * @param message the message to send to the log
     */
    fun debug(message: String) {
        if (debugging) {
            log(Level.WARNING, "[DEBUG] $message")
        }
    }

    /**
     * Shuts down all handlers and the reader.
     */
    fun shutdownAll() {
        for (handler in handlers) {
            handler.close()
        }
    }


}