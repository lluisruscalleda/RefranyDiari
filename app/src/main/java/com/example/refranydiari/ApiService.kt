import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class RefranysRequest(val sentences: List<String>)

suspend fun fetchGroupedRefranys(sentences: List<String>): Map<String, List<String>> {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    val response: Map<String, List<String>> = client.post("https://refranydiari-api.onrender.com/group-refranys") {
        contentType(ContentType.Application.Json)
        setBody(RefranysRequest(sentences))
    }.body()

    client.close()

    return response
}
