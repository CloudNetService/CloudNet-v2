package eu.cloudnetservice.cloudnet.v2.logging

import java.io.IOException
import java.io.PrintWriter
import java.io.StringWriter
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.logging.Formatter
import java.util.logging.LogRecord

class LoggingFormatter : Formatter() {
    private val dateFormat: DateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm:ss")
    override fun format(record: LogRecord?): String {
        val builder = StringBuilder()
        if (record != null) {
            val message = formatMessage(record)
            builder.append('[')
                    .append(dateFormat.format(record.millis))
                    .append("] ")
                    .append(record.level.name)
                    .append(": ")
                    .append(message)

            if (!message.endsWith(System.lineSeparator())) {
                builder.append(System.lineSeparator())
            }

            if (record.thrown != null) {
                try {
                    StringWriter().use { writer ->
                        record.thrown.printStackTrace(PrintWriter(writer))
                        builder.append(writer).append(System.lineSeparator())
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

        }

        return builder.toString()
    }
}