package data.remote

import data.model.Post
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.serialization.SerializationException

import util.Result
import util.NetworkError

class PostsApiClient(
    private val httpClient: HttpClient
) {
    private val baseUrl = "http://44.218.219.212:3000/api/posts/"

    suspend fun getPosts(): Result<List<Post>, NetworkError> {
        return try {
            val response = httpClient.get(baseUrl)

            when(response.status.value) {
                in 200..299 -> {
                    val posts = response.body<List<Post>>()
                    Result.Success(posts)
                }
                401 -> Result.Error(NetworkError.UNAUTHORIZED)
                408 -> Result.Error(NetworkError.REQUEST_TIMEOUT)
                in 500..599 -> Result.Error(NetworkError.SERVER_ERROR)
                else -> Result.Error(NetworkError.UNKNOWN)
            }
        } catch(e: UnresolvedAddressException) {
            Result.Error(NetworkError.NO_INTERNET)
        } catch(e: SerializationException) {
            Result.Error(NetworkError.SERIALIZATION)
        } catch(e: Exception) {
            Result.Error(NetworkError.UNKNOWN)
        }
    }

    suspend fun createPost(post: Post): Result<Post, NetworkError> {
        return try {
            val response = httpClient.post(baseUrl) {
                contentType(ContentType.Application.Json)
                setBody(post)
            }

            when(response.status.value) {
                in 200..299 -> {
                    val createdPost = response.body<Post>()
                    Result.Success(createdPost)
                }
                401 -> Result.Error(NetworkError.UNAUTHORIZED)
                409 -> Result.Error(NetworkError.CONFLICT)
                408 -> Result.Error(NetworkError.REQUEST_TIMEOUT)
                413 -> Result.Error(NetworkError.PAYLOAD_TOO_LARGE)
                in 500..599 -> Result.Error(NetworkError.SERVER_ERROR)
                else -> Result.Error(NetworkError.UNKNOWN)
            }
        } catch(e: UnresolvedAddressException) {
            Result.Error(NetworkError.NO_INTERNET)
        } catch(e: SerializationException) {
            Result.Error(NetworkError.SERIALIZATION)
        } catch(e: Exception) {
            Result.Error(NetworkError.UNKNOWN)
        }
    }
}