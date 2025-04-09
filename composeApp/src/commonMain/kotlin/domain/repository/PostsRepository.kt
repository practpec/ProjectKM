package domain.repository

import data.model.Post
import data.remote.PostsApiClient
import util.NetworkError
import util.Result

class PostsRepository(
    private val apiClient: PostsApiClient
) {
    suspend fun getPosts(): Result<List<Post>, NetworkError> {
        return apiClient.getPosts()
    }

    suspend fun createPost(post: Post): Result<Post, NetworkError> {
        return apiClient.createPost(post)
    }
}
