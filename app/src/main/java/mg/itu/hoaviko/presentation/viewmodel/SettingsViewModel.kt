package mg.itu.hoaviko.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import mg.itu.hoaviko.HoavikoApplication
import mg.itu.hoaviko.data.HoavikoRepository
import mg.itu.hoaviko.data.entity.PaymentChannel
import mg.itu.hoaviko.data.entity.UserAccount
import mg.itu.hoaviko.ui.i18n.UiMessage
import mg.itu.hoaviko.util.PasswordHasher

/** Réglages du compte (langue, modes de paiement, objectif, établissement). */
@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModel(
    private val repository: HoavikoRepository,
    private val sessionState: SessionState
) : ViewModel() {

    val user: StateFlow<UserAccount?> = sessionState.userFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val channels: StateFlow<List<PaymentChannel>> = repository.channels
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _event = MutableStateFlow<UiMessage?>(null)
    val event: StateFlow<UiMessage?> = _event.asStateFlow()

    fun consumeEvent() {
        _event.value = null
    }

    fun setLanguage(code: String) = applyToCurrentUser { it.copy(language = code) }

    fun setPaymentChannel(id: Long) = applyToCurrentUser { it.copy(paymentChannelId = id) }

    fun setSavingsChannel(id: Long) = applyToCurrentUser { it.copy(savingsChannelId = id) }

    /** Enregistre objectif + cotisation mensuelle. Renvoie false si montants invalides. */
    fun saveObjective(objectiveText: String, monthlyText: String): Boolean {
        val objective = objectiveText.toLongOrNull()
        val monthly = monthlyText.toLongOrNull()
        if (objective == null || objective < 0 || monthly == null || monthly <= 0) {
            _event.value = UiMessage.INVALID_AMOUNT
            return false
        }
        applyToCurrentUser { it.copy(objectiveAmount = objective, monthlyContribution = monthly) }
        _event.value = UiMessage.SETTINGS_SAVED
        return true
    }

    private fun applyToCurrentUser(transform: (UserAccount) -> UserAccount) {
        val current = user.value ?: return
        viewModelScope.launch { repository.updateUser(transform(current)) }
    }

    /** Vérifie une demande de changement de mot de passe. Message d'erreur ou null si valide. */
    fun passwordChangeError(current: String, new: String, confirm: String): UiMessage? {
        val account = user.value ?: return null
        return when {
            current.isBlank() || new.isBlank() || confirm.isBlank() -> UiMessage.FILL_ALL_FIELDS
            new.length < 6 -> UiMessage.PASSWORD_SHORT
            new != confirm -> UiMessage.PASSWORD_MISMATCH
            !PasswordHasher.verify(current.trim(), account.salt, account.passwordHash) -> UiMessage.WRONG_PASSWORD
            else -> null
        }
    }

    /** Émet directement un message d'état (erreurs de validation locale). */
    fun reportMessage(message: UiMessage) {
        _event.value = message
    }

    /** Applique le nouveau mot de passe (la demande a déjà été confirmée). */
    fun changePassword(newPassword: String) {
        val account = user.value ?: return
        val salt = PasswordHasher.newSalt()
        viewModelScope.launch {
            repository.updateUser(
                account.copy(salt = salt, passwordHash = PasswordHasher.hash(newPassword, salt))
            )
            _event.value = UiMessage.PASSWORD_CHANGED
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as HoavikoApplication
                SettingsViewModel(app.repository, app.session)
            }
        }
    }
}