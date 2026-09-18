package mg.itu.hoaviko.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import java.time.LocalDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mg.itu.hoaviko.HoavikoApplication
import mg.itu.hoaviko.data.HoavikoRepository
import mg.itu.hoaviko.data.entity.Contribution
import mg.itu.hoaviko.data.entity.PaymentChannel
import mg.itu.hoaviko.data.entity.UserAccount
import mg.itu.hoaviko.data.entity.Withdrawal
import mg.itu.hoaviko.domain.AccountRules
import mg.itu.hoaviko.domain.HistoryPeriod
import mg.itu.hoaviko.domain.WithdrawalRules
import mg.itu.hoaviko.ui.i18n.UiMessage
import mg.itu.hoaviko.util.PasswordHasher
import mg.itu.hoaviko.data.remote.FirebaseDataService

/** Formulaire de versement. */
data class DepositForm(
    val amount: String = "",
    val channelId: Long = 0L,
    val accountRef: String = "",
    val password: String = ""
)

/** Formulaire de demande de retrait. */
data class WithdrawalForm(
    val amount: String = "",
    val hasNotice: Boolean = true,
    val justification: String = "",
    val password: String = ""
)

/** Résultat d'un versement confirmé. */
data class DepositSuccess(val amount: Long, val newTotal: Long)

/** Résultat d'une demande de retrait envoyée. */
data class WithdrawalSuccess(val amount: Long, val percent: Int, val penalty: Long, val net: Long)

/** Espace Suivi : versements, totaux, historique filtré, confirmations. */
@OptIn(ExperimentalCoroutinesApi::class)
class TrackingViewModel(
    private val repository: HoavikoRepository,
    private val sessionState: SessionState,
    private val firebaseDataService: FirebaseDataService =
        FirebaseDataService()
) : ViewModel() {

    private val todayEpochDay = LocalDate.now().toEpochDay()

    val user: StateFlow<UserAccount?> = sessionState.userFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val channels: StateFlow<List<PaymentChannel>> = repository.channels
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val contributions: StateFlow<List<Contribution>> = sessionState.validUserId
        .flatMapLatest { id ->
            if (id <= 0) flowOf(emptyList()) else repository.observeContributions(id)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val totalContributed: StateFlow<Long> = sessionState.validUserId
        .flatMapLatest { id ->
            if (id <= 0) flowOf(0L) else repository.observeTotal(id)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0L)

    val paymentCount: StateFlow<Int> = sessionState.validUserId
        .flatMapLatest { id ->
            if (id <= 0) flowOf(0) else repository.observeCount(id)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    val myWithdrawals: StateFlow<List<Withdrawal>> = sessionState.validUserId
        .flatMapLatest { id ->
            if (id <= 0) flowOf(emptyList()) else repository.observeWithdrawals(id)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _period = MutableStateFlow(HistoryPeriod.ALL)
    val period: StateFlow<HistoryPeriod> = _period.asStateFlow()

    private val _depositForm = MutableStateFlow(DepositForm())
    val depositForm: StateFlow<DepositForm> = _depositForm.asStateFlow()

    private val _withdrawalForm = MutableStateFlow(WithdrawalForm())
    val withdrawalForm: StateFlow<WithdrawalForm> = _withdrawalForm.asStateFlow()

    private val _event = MutableStateFlow<UiMessage?>(null)
    val event: StateFlow<UiMessage?> = _event.asStateFlow()

    private val _success = MutableStateFlow<DepositSuccess?>(null)
    val success: StateFlow<DepositSuccess?> = _success.asStateFlow()

    private val _withdrawalSuccess = MutableStateFlow<WithdrawalSuccess?>(null)
    val withdrawalSuccess: StateFlow<WithdrawalSuccess?> = _withdrawalSuccess.asStateFlow()

    val today: Long get() = todayEpochDay

    /** Âge révolu de l'utilisateur courant (0 si inconnu). */
    val userAge: Int get() =
        user.value?.let { AccountRules.currentAge(it.birthDateEpochDay, todayEpochDay) } ?: 0

    fun consumeEvent() {
        _event.value = null
    }

    fun consumeSuccess() {
        _success.value = null
    }

    fun consumeWithdrawalSuccess() {
        _withdrawalSuccess.value = null
    }

    fun onPeriodChange(period: HistoryPeriod) = _period.update { period }

    fun onAmountChange(value: String) = _depositForm.update { it.copy(amount = value.filter(Char::isDigit)) }
    fun onChannelChange(id: Long) = _depositForm.update { it.copy(channelId = id) }
    fun onAccountRefChange(value: String) = _depositForm.update { it.copy(accountRef = value) }
    fun onPasswordChange(value: String) = _depositForm.update { it.copy(password = value) }

    fun selectDefaultChannel() {
        val current = _depositForm.value
        if (current.channelId == 0L) {
            channels.value.firstOrNull()?.let { channel ->
                _depositForm.update { it.copy(channelId = channel.id) }
            }
        }
    }

    /** Confirme le versement via le mot de passe (opération irréversible). */
    fun confirmDeposit() {
        val form = _depositForm.value
        val amount = form.amount.toLongOrNull() ?: 0L
        if (amount <= 0) {
            _event.value = UiMessage.INVALID_AMOUNT
            return
        }
        val reference = form.accountRef.trim()
        if (reference.isEmpty()) {
            _event.value = UiMessage.FILL_ALL_FIELDS
            return
        }
        val targetUser = user.value ?: return
        if (!PasswordHasher.verify(form.password, targetUser.salt, targetUser.passwordHash)) {
            _event.value = UiMessage.WRONG_PASSWORD
            return
        }
        val channelId = if (form.channelId != 0L) form.channelId else (channels.value.firstOrNull()?.id ?: 0L)
        val newTotal = totalContributed.value + amount
        viewModelScope.launch {
            val contribution = Contribution(
                userId = targetUser.id,
                amountAriary = amount,
                dateEpochDay = todayEpochDay,
                paymentChannelId = channelId,
                accountReference = reference,
                note = ""
            )

            // Room en premier : l'opération fonctionne même hors ligne.
            val localId = repository.addContribution(contribution)

            // Firestore reçoit la cotisation avec son identifiant local.
            firebaseDataService.queueContribution(
                firebaseUid = targetUser.firebaseUid,
                contribution = contribution.copy(id = localId)
            )
        }
        _success.value = DepositSuccess(amount, newTotal)
        _depositForm.update { DepositForm(channelId = it.channelId) }
        _period.value = HistoryPeriod.ALL
    }

    // ---- Demande de retrait ----

    fun onWithdrawalAmountChange(value: String) =
        _withdrawalForm.update { it.copy(amount = value.filter(Char::isDigit)) }

    fun onNoticeChange(hasNotice: Boolean) = _withdrawalForm.update { it.copy(hasNotice = hasNotice) }

    fun onWithdrawalJustificationChange(value: String) =
        _withdrawalForm.update { it.copy(justification = value) }

    fun onWithdrawalPasswordChange(value: String) =
        _withdrawalForm.update { it.copy(password = value) }

    /** Pénalité (en %) prévue pour le type de retrait choisi. */
    fun penaltyPercentFor(hasNotice: Boolean): Int = WithdrawalRules.penaltyPercent(hasNotice, userAge)

    /** Envoie la demande de retrait après confirmation par mot de passe. */
    fun submitWithdrawal() {
        val form = _withdrawalForm.value
        val amount = form.amount.toLongOrNull() ?: 0L
        if (amount <= 0) {
            _event.value = UiMessage.INVALID_AMOUNT
            return
        }
        if (totalContributed.value < amount) {
            _event.value = UiMessage.INSUFFICIENT_BALANCE
            return
        }
        val justification = form.justification.trim()
        if (justification.isEmpty()) {
            _event.value = UiMessage.JUSTIFICATION_REQUIRED
            return
        }
        val targetUser = user.value ?: return
        if (!PasswordHasher.verify(form.password, targetUser.salt, targetUser.passwordHash)) {
            _event.value = UiMessage.WRONG_PASSWORD
            return
        }
        val percent = WithdrawalRules.penaltyPercent(form.hasNotice, userAge)
        val penalty = WithdrawalRules.penaltyAmount(amount, percent)
        viewModelScope.launch {
            val withdrawal = Withdrawal(
                userId = targetUser.id,
                amountAriary = amount,
                requestDateEpochDay = todayEpochDay,
                withdrawalDateEpochDay = todayEpochDay,
                hasNotice = false,
                penaltyPercent = percent,
                status = Withdrawal.STATUS_PENDING,
                note = justification
            )

            // Enregistrement local Room.
            val localId = repository.addWithdrawal(withdrawal)

            // Mise en attente de la synchronisation Firestore.
            firebaseDataService.queueWithdrawal(
                firebaseUid = targetUser.firebaseUid,
                withdrawal = withdrawal.copy(id = localId)
            )

            _withdrawalSuccess.value = WithdrawalSuccess(
                amount = amount,
                percent = percent,
                penalty = penalty,
                net = amount - penalty
            )

            _withdrawalForm.value = WithdrawalForm()
        }
        _withdrawalSuccess.value = WithdrawalSuccess(amount, percent, penalty, amount - penalty)
        _withdrawalForm.value = WithdrawalForm()
    }

    fun filteredContributions(contributions: List<Contribution>, period: HistoryPeriod): List<Contribution> =
        contributions.filter { period.isIncluded(it.dateEpochDay, todayEpochDay) }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as HoavikoApplication
                TrackingViewModel(app.repository, app.session)
            }
        }
    }
}