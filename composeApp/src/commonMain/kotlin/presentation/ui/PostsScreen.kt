package presentation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import data.model.Post
import presentation.viewmodel.PostsEvent
import presentation.viewmodel.PostsState
import presentation.viewmodel.PostsViewModel


@Composable
fun PostsScreen(viewModel: PostsViewModel) {
    val state = viewModel.state
    val showModal = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        viewModel.onEvent(PostsEvent.LoadPosts)
    }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Posts App",
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.Bold,
            )

            Button(
                onClick = { showModal.value = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Crear nuevo Post")
            }

            // Mostrar el modal cuando showModal sea true
            if (showModal.value) {
                PostCreateDialog(viewModel, state, onDismiss = { showModal.value = false })
            }

            // Error message
            AnimatedVisibility(visible = state.error != null) {
                state.error?.let {
                    Text(
                        text = "Error: ${it.name}",
                        color = Color.Red,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Text(
                text = "Posts",
                style = MaterialTheme.typography.h6,
            )

            // Loading indicator for posts list
            if (state.isLoading && state.posts.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }


            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.posts) { post ->
                    PostItem(post = post)
                }
            }
        }

}

@Composable
fun PostCreateDialog(viewModel: PostsViewModel, state: PostsState, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Crear nuevo Post",
                    style = MaterialTheme.typography.h6
                )

                OutlinedTextField(
                    value = state.id,
                    onValueChange = { viewModel.onEvent(PostsEvent.UpdateId(it)) },
                    label = { Text("ID") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = state.title,
                    onValueChange = { viewModel.onEvent(PostsEvent.UpdateTitle(it)) },
                    label = { Text("Titulo") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = state.body,
                    onValueChange = { viewModel.onEvent(PostsEvent.UpdateBody(it)) },
                    label = { Text("Descripcion") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = state.userId,
                    onValueChange = { viewModel.onEvent(PostsEvent.UpdateUserId(it)) },
                    label = { Text("Id usuario") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = { viewModel.onEvent(PostsEvent.ClearForm) },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Limpiar")
                    }

                    Button(
                        onClick = { viewModel.onEvent(PostsEvent.CreatePost) }
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )
                        } else {
                            Text("Crear Post")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PostItem(post: Post) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "ID: ${post.id}",
                    style = MaterialTheme.typography.caption,
                )
                Text(
                    text = "ID de usuario: ${post.userId}",
                    style = MaterialTheme.typography.caption,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = post.title,
                style = MaterialTheme.typography.subtitle1,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = post.body,
                style = MaterialTheme.typography.body2,
            )
        }
    }
}
