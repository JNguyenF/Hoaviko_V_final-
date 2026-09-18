package mg.itu.hoaviko.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import mg.itu.hoaviko.HoavikoApplication
import mg.itu.hoaviko.data.HoavikoRepository
import mg.itu.hoaviko.data.entity.UserAccount
import mg.itu.hoaviko.data.remote.FirebaseAuthService

/** Racine : expose l'utilisateur connecté et la déconnexion. */
@OptIn(ExperimentalCoroutinesApi::class)
class SessionViewModel(
    repository: HoavikoRepository,
    private val sessionState: SessionState,
    private val firebaseAuthService: FirebaseAuthService = FirebaseAuthService()
) : ViewModel() {

    val currentUser: StateFlow<UserAccount?> = sessionState.userFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun logout() {
        firebaseAuthService.signOut()
        sessionState.logout()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as HoavikoApplication
                SessionViewModel(app.repository, app.session)
            }
        }
    }
}