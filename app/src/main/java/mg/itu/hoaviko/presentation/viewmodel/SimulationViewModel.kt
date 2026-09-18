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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mg.itu.hoaviko.HoavikoApplication
import mg.itu.hoaviko.data.HoavikoRepository
import mg.itu.hoaviko.data.entity.PaymentChannel
import mg.itu.hoaviko.domain.AccountRules
import mg.itu.hoaviko.domain.RETIREMENT_AGE
import mg.itu.hoaviko.domain.RetirementCalculator
import mg.itu.hoaviko.domain.RetirementSummary

/** Âge actuel (fixe, déduit de la date de naissance) via le clavier d'entiers. */
data class SimulationInput(
    val currentAge: String = "",
    val monthlyContribution: String = "100000",
    val initialSavings: String = "0",
    val savingsChannelId: Long = 0L
)

/** Écran de simulation : l'âge actuel est déduit de l'année de naissance (non modifiable). */
@OptIn(ExperimentalCoroutinesApi::class)
class SimulationViewModel(
    private val repository: HoavikoRepository,
    private val sessionState: SessionState
) : ViewModel() {

    private val todayEpochDay = LocalDate.now().toEpochDay()

    /** Banques disponibles (catégorie Banque) pour le taux d'intérêt. */
    val banks: StateFlow<List<PaymentChannel>> = repository.channels
        .map { list -> list.filter { it.category == "Banque" } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** Âge (fixe) de l'utilisateur connecté, calculé depuis sa date de naissance. */
    private val _ageNow = MutableStateFlow(0)
    val ageNow: StateFlow<Int> = _ageNow.asStateFlow()

    /** Âge de retraite fixé par les règles de l'application. */
    val retirementAge: Int = RETIREMENT_AGE

    private val _input = MutableStateFlow(SimulationInput())
    val input: StateFlow<SimulationInput> = _input.asStateFlow()

    val result: StateFlow<RetirementSummary?> =
        combine(_input, banks) { input, banks ->
            input.toSummary(banks)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    init {
        viewModelScope.launch {
            val userId = sessionState.userId.value.takeIf { it != null && it > 0 }
            val user = userId?.let { repository.getUser(it) }
            val age = user?.let { AccountRules.currentAge(it.birthDateEpochDay, todayEpochDay) } ?: 0
            val defaultValue = user?.savingsChannelId ?: 0L
            val fallback = if (defaultValue <= 0) {
                repository.getAllChannels().firstOrNull { it.category == "Banque" }?.id ?: 0L
            } else defaultValue
            _ageNow.value = age
            _input.update {
                it.copy(
                    currentAge = if (age > 0) age.toString() else "",
                    savingsChannelId = fallback
                )
            }
        }
    }

    fun onMonthlyChange(value: String) = _input.update { it.copy(monthlyContribution = value.digits()) }
    fun onInitialSavingsChange(value: String) = _input.update { it.copy(initialSavings = value.digits()) }
    fun onSavingsChannelChange(id: Long) = _input.update { it.copy(savingsChannelId = id) }

    /** Applique le plan simulé au compte (cotisation + banque d'épargne). */
    fun applyToProfile() {
        val current = _input.value.monthlyContribution.toLongOrNull() ?: return
        val channel = _input.value.savingsChannelId
        if (current <= 0) return
        viewModelScope.launch {
            val user = repository.getUser(sessionState.userId.value ?: return@launch)
            if (user != null) {
                repository.updateUser(user.copy(monthlyContribution = current, savingsChannelId = channel))
            }
        }
    }

    private fun SimulationInput.toSummary(banks: List<PaymentChannel>): RetirementSummary? {
        val current = currentAge.toIntOrNull() ?: return null
        val retirement = RETIREMENT_AGE
        val monthly = monthlyContribution.toLongOrNull() ?: return null
        val initial = initialSavings.toLongOrNull() ?: return null
        if (current < 18 || retirement <= current || monthly <= 0) return null
        val rate = banks.firstOrNull { it.id == savingsChannelId }?.annualRate
            ?: banks.firstOrNull()?.annualRate
            ?: 0.06
        val months = RetirementCalculator.monthsUntilRetirement(current, retirement)
        return RetirementCalculator.compute(initial.toDouble(), monthly.toDouble(), rate, months)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as HoavikoApplication
                SimulationViewModel(app.repository, app.session)
            }
        }
    }
}

private fun String.digits(): String = filter(Char::isDigit)