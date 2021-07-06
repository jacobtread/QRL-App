package nz.qrl.app

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object NetUtil {

    fun get(url: String): String {
        with(URL(url).openConnection()) {
            BufferedReader(InputStreamReader(inputStream)).use {
                val lines = StringBuilder()
                var line: String?
                while (true) {
                    line = it.readLine()
                    if (line == null) break
                    lines.append(line).append("\n")
                }
                return lines.toString()
            }
        }
    }

    fun post(url: String, data: HashMap<String, String>): String {
        with(URL(url).openConnection() as HttpURLConnection) {
            requestMethod = "POST"
            doOutput = true
            outputStream.use {
                val body = buildBody(data)
                it.write(body.toByteArray(StandardCharsets.UTF_8))
                it.flush()
            }
            BufferedReader(InputStreamReader(inputStream)).use {
                val lines = StringBuilder()
                var line: String?
                while (true) {
                    line = it.readLine()
                    if (line == null) break
                    lines.append(line).append("\n")
                }
                return lines.toString()
            }
        }
    }

    private fun buildBody(data: HashMap<String, String>): String {
        val outputBuilder = StringBuilder()
        data.forEach { (key, value) ->
            outputBuilder.append(URLEncoder.encode(key, "UTF-8"))
                .append("=")
                .append(URLEncoder.encode(value, "UTF-8"))
                .append("&")
        }
        val output = outputBuilder.toString()

        return if (output.isNotEmpty()) {
            output.substring(0 until output.length - 1)
        } else output
    }

}