package mg.itu.hoaviko.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import java.time.LocalDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import mg.itu.hoaviko.HoavikoApplication
import mg.itu.hoaviko.data.HoavikoRepository
import mg.itu.hoaviko.data.entity.PaymentChannel
import mg.itu.hoaviko.data.entity.UserAccount
import mg.itu.hoaviko.domain.AccountRules
import mg.itu.hoaviko.domain.RETIREMENT_AGE
import mg.itu.hoaviko.domain.RetirementCalculator
import mg.itu.hoaviko.domain.RetirementSummary

/** Écran d'accueil : données personnalisées et synthèse du plan. */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    repository: HoavikoRepository,
    private val sessionState: SessionState
) : ViewModel() {
    private val todayEpochDay = LocalDate.now().toEpochDay()

    val user: StateFlow<UserAccount?> = sessionState.userFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val channels: StateFlow<List<PaymentChannel>> = repository.channels
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val totalContributed: StateFlow<Long> = sessionState.validUserId
        .flatMapLatest { id ->
            if (id <= 0) flowOf(0L) else repository.observeTotal(id)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0L)

    val paymentCount: StateFlow<Int> = sessionState.validUserId
        .flatMapLatest { id ->
            if (id <= 0) flowOf(0) else repository.observeCount(id)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    /** Nombre de comptes membres (statistique de l'accueil administrateur). */
    val memberCount: StateFlow<Int> = repository.observeMemberCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    /** Nombre de demandes de retrait reçues (statistique de l'accueil administrateur). */
    val withdrawalCount: StateFlow<Int> = repository.observeWithdrawalCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    /** Synthèse du plan d'épargne jusqu'à 60 ans (taux de la banque choisie). */
    val summary: StateFlow<RetirementSummary?> =
        combine(user, channels) { user, channels ->
            val u = user ?: return@combine null
            if (u.monthlyContribution <= 0) return@combine null
            val age = AccountRules.currentAge(u.birthDateEpochDay, todayEpochDay)
            val months = RetirementCalculator.monthsUntilRetirement(age, RETIREMENT_AGE)
            val rate = channels.firstOrNull { it.id == u.savingsChannelId }?.annualRate
                ?: channels.firstOrNull { it.category == "Banque" }?.annualRate
                ?: 0.06
            RetirementCalculator.compute(0.0, u.monthlyContribution.toDouble(), rate, months)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as HoavikoApplication
                HomeViewModel(app.repository, app.session)
            }
        }
    }
}