package presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import data.model.Post
import domain.repository.PostsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import util.NetworkError
import util.onError
import util.onSuccess

class PostsViewModel(
    private val repository: PostsRepository,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) {
    var state by mutableStateOf(PostsState())
        private set

    fun onEvent(event: PostsEvent) {
        when (event) {
            is PostsEvent.LoadPosts -> loadPosts()
            is PostsEvent.CreatePost -> createPost()
            is PostsEvent.UpdateId -> state = state.copy(id = event.id)
            is PostsEvent.UpdateTitle -> state = state.copy(title = event.title)
            is PostsEvent.UpdateBody -> state = state.copy(body = event.body)
            is PostsEvent.UpdateUserId -> state = state.copy(userId = event.userId)
            is PostsEvent.ClearForm -> clearForm()
        }
    }

    private fun loadPosts() {
        state = state.copy(isLoading = true, error = null)

        coroutineScope.launch {
            repository.getPosts()
                .onSuccess { posts ->
                    state = state.copy(
                        posts = posts,
                        isLoading = false
                    )
                }
                .onError { error ->
                    state = state.copy(
                        error = error,
                        isLoading = false
                    )
                }
        }
    }

    private fun createPost() {
        val id = state.id.toIntOrNull() ?: return
        val userId = state.userId.toIntOrNull() ?: return

        if (state.title.isBlank() || state.body.isBlank()) return

        val post = Post(
            id = id,
            title = state.title,
            body = state.body,
            userId = userId
        )

        state = state.copy(isLoading = true, error = null)

        coroutineScope.launch {
            repository.createPost(post)
                .onSuccess { createdPost ->
                    state = state.copy(
                        posts = state.posts + createdPost,
                        isLoading = false
                    )
                    clearForm()
                }
                .onError { error ->
                    state = state.copy(
                        error = error,
                        isLoading = false
                    )
                }
        }
    }

    private fun clearForm() {
        state = state.copy(
            id = "",
            title = "",
            body = "",
            userId = ""
        )
    }
}

data class PostsState(
    val posts: List<Post> = emptyList(),
    val id: String = "",
    val title: String = "",
    val body: String = "",
    val userId: String = "",
    val isLoading: Boolean = false,
    val error: NetworkError? = null
)

sealed class PostsEvent {
    object LoadPosts : PostsEvent()
    object CreatePost : PostsEvent()
    data class UpdateId(val id: String) : PostsEvent()
    data class UpdateTitle(val title: String) : PostsEvent()
    data class UpdateBody(val body: String) : PostsEvent()
    data class UpdateUserId(val userId: String) : PostsEvent()
    object ClearForm : PostsEvent()
}
