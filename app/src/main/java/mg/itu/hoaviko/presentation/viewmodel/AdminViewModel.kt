package mg.itu.hoaviko.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import mg.itu.hoaviko.HoavikoApplication
import mg.itu.hoaviko.data.HoavikoRepository
import mg.itu.hoaviko.data.dao.WithdrawalWithUser
import mg.itu.hoaviko.data.entity.UserAccount
import mg.itu.hoaviko.data.entity.Withdrawal
import mg.itu.hoaviko.ui.i18n.UiMessage

/** Espace administrateur : suivis des demandes de retrait et gestion des comptes. */
class AdminViewModel(
    private val repository: HoavikoRepository
) : ViewModel() {

    val requests: StateFlow<List<WithdrawalWithUser>> = repository.observeAllWithdrawalsWithUser()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** Tous les comptes membres (les comptes admin ne sont ni listés ni supprimables). */
    val users: StateFlow<List<UserAccount>> = repository.observeAllUsers()
        .map { list -> list.filter { it.role != UserAccount.ROLE_ADMIN } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _event = MutableStateFlow<UiMessage?>(null)
    val event: StateFlow<UiMessage?> = _event.asStateFlow()

    fun consumeEvent() {
        _event.value = null
    }

    fun approve(id: Long) {
        viewModelScope.launch {
            repository.updateWithdrawalStatus(id, Withdrawal.STATUS_APPROVED)
            _event.value = UiMessage.WITHDRAWAL_APPROVED
        }
    }

    fun reject(id: Long) {
        viewModelScope.launch {
            repository.updateWithdrawalStatus(id, Withdrawal.STATUS_REJECTED)
            _event.value = UiMessage.WITHDRAWAL_REJECTED
        }
    }

    fun deleteUser(id: Long) {
        viewModelScope.launch {
            repository.deleteUser(id)
            _event.value = UiMessage.USER_DELETED
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as HoavikoApplication
                AdminViewModel(app.repository)
            }
        }
    }
}