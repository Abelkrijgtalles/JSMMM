import groovy.json.JsonOutput
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.UUID

fun mixinCompatibilityLevelFor(javaVersion: String): String {
    val level = javaVersion.toInt()
    return "JAVA_$level"
}

fun uploadModrinthVersion(
    projectId: String,
    token: String,
    primaryFile: File,
    additionalFiles: Map<File, String> = emptyMap(), // file -> file_type (e.g. "signature")
    versionNumber: String,
    versionName: String = versionNumber,
    changelog: String = "",
    gameVersions: List<String>,
    loaders: List<String>,
    versionType: String = "release",
) {
    val boundary = "----ModrinthUpload${UUID.randomUUID()}"

    fun esc(s: String) = s.replace("\\", "\\\\").replace("\"", "\\\"")
    fun jsonArr(items: List<String>) = items.joinToString(",", "[", "]") { "\"${esc(it)}\"" }

    val fileParts = listOf("primary") + additionalFiles.keys.mapIndexed { i, _ -> "extra$i" }
    val fileTypesJson = additionalFiles.entries
        .mapIndexed { i, (_, type) -> "\"extra$i\":\"${esc(type)}\"" }
        .joinToString(",", "{", "}")

    val dataJson = JsonOutput.toJson(mapOf(
          "name" to versionName,
          "version_number" to versionNumber,
          "changelog" to changelog,
          "dependencies" to emptyList<Any>(),
          "game_versions" to gameVersions,
          "version_type" to versionType,
          "loaders" to loaders,
          "featured" to true,
          "status" to "listed",
          "project_id" to projectId,
          "file_parts" to fileParts,
          "primary_file" to "primary",
          "file_types" to additionalFiles.values.mapIndexed { i, type -> "extra$i" to type }.toMap()
    ))

    val body = ByteArrayOutputStream()
    fun writePart(name: String, filename: String? = null, contentType: String? = null, content: ByteArray) {
        body.write("--$boundary\r\n".toByteArray())
        body.write(
            ("Content-Disposition: form-data; name=\"$name\"" +
                    (filename?.let { "; filename=\"$it\"" } ?: "") + "\r\n")
                .toByteArray()
        )
        contentType?.let { body.write("Content-Type: $it\r\n".toByteArray()) }
        body.write("\r\n".toByteArray())
        body.write(content)
        body.write("\r\n".toByteArray())
    }

    writePart("data", contentType = "application/json", content = dataJson.toByteArray())
    writePart("primary", filename = primaryFile.name, contentType = "application/octet-stream", content = primaryFile.readBytes())
    additionalFiles.keys.forEachIndexed { i, file ->
        writePart("extra$i", filename = file.name, contentType = "application/octet-stream", content = file.readBytes())
    }
    body.write("--$boundary--\r\n".toByteArray())

    val request = HttpRequest.newBuilder()
        .uri(URI.create("https://api.modrinth.com/v2/version"))
        .header("Authorization", token)
        .header("Content-Type", "multipart/form-data; boundary=$boundary")
        .POST(HttpRequest.BodyPublishers.ofByteArray(body.toByteArray()))
        .build()

    val response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString())
    if (response.statusCode() !in 200..299) {
        error("Modrinth upload of ${primaryFile.name} failed (${response.statusCode()}): ${response.body()}")
    }
    println("Uploaded ${primaryFile.name} to Modrinth as $versionNumber")
}