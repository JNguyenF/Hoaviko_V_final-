package mg.itu.hoaviko.presentation.viewmodel

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import mg.itu.hoaviko.data.HoavikoRepository
import mg.itu.hoaviko.data.SessionManager
import mg.itu.hoaviko.data.entity.UserAccount

/**
 * État de session partagé : utilisateur courant observé en continu
 * et mise à jour de la session (connexion / déconnexion).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SessionState(
    private val repository: HoavikoRepository,
    private val session: SessionManager
) {
    private val _userId = MutableStateFlow(session.currentUserId.takeIf { it > 0 })

    /** Id utilisateur courant (null si déconnecté). */
    val userId: StateFlow<Long?> = _userId.asStateFlow()

    /** Flux utilisateur courant (null si déconnecté). */
    val userFlow: Flow<UserAccount?> = _userId.flatMapLatest { id ->
        if (id == null) flowOf(null) else repository.observeUser(id)
    }

    /** Flux d'id valide (-1 si déconnecté) pour les requêtes par utilisateur. */
    val validUserId: Flow<Long> = _userId.map { it ?: -1L }

    fun setCurrentUser(id: Long) {
        session.currentUserId = id
        _userId.value = id
    }

    fun logout() {
        session.logout()
        _userId.value = null
    }
}