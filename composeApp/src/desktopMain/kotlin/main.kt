import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.ktor.client.engine.okhttp.OkHttp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.swing.Swing

fun main() {
    // Initialize Swing dispatcher for desktop
    Dispatchers.Swing

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Posts App",
        ) {
            App(engine = OkHttp.create())
        }
    }
}
