package mg.itu.hoaviko.ui.auth

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import mg.itu.hoaviko.presentation.viewmodel.AuthViewModel
import mg.itu.hoaviko.ui.i18n.LocalAppStrings
import mg.itu.hoaviko.ui.i18n.UiMessage
import mg.itu.hoaviko.ui.i18n.text

/** Affiche en snackbar les messages d'événement du AuthViewModel puis les consomme. */
@Composable
fun AuthEventSnackbar(viewModel: AuthViewModel, snackbarHostState: SnackbarHostState) {
    val strings = LocalAppStrings.current
    val scope = rememberCoroutineScope()
    val event by viewModel.event.collectAsStateWithLifecycle()

    LaunchedEffect(event) {
        // LOGIN_SUCCESS / REGISTER_SUCCESS sont gérés globalement (bascule d'écran).
        val current = event ?: return@LaunchedEffect
        if (current == UiMessage.LOGIN_SUCCESS || current == UiMessage.REGISTER_SUCCESS) return@LaunchedEffect
        val message = current.text(strings)
        // Consomme immédiatement : le prochain événement identique réémettra bien.
        viewModel.consumeEvent()
        scope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }
}