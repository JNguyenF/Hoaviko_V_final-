package mg.itu.hoaviko.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mg.itu.hoaviko.HoavikoApplication
import mg.itu.hoaviko.data.HoavikoRepository
import mg.itu.hoaviko.data.entity.UserAccount
import mg.itu.hoaviko.domain.AccountRules
import mg.itu.hoaviko.ui.i18n.UiMessage
import mg.itu.hoaviko.util.PasswordHasher
import mg.itu.hoaviko.data.remote.AccountCreationResult
import mg.itu.hoaviko.data.remote.FirebaseAuthService
import mg.itu.hoaviko.data.remote.AccountLoginResult
import mg.itu.hoaviko.data.remote.FirebaseProfileService
data class LoginForm(
    val identifier: String = "",
    val password: String = ""
)

data class RegisterForm(
    val lastName: String = "",
    val firstName: String = "",
    val birthDateEpochDay: Long? = null,
    val cinNumber: String = "",
    val profession: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val acceptedPolicy: Boolean = false
)

data class ForgotForm(
    val email: String = "",
    val cinNumber: String = "",
    val newPassword: String = "",
    val confirmPassword: String = ""
)

/** Authentification : connexion, inscription et réinitialisation du mot de passe. */
class AuthViewModel(
    private val repository: HoavikoRepository,
    private val sessionState: SessionState,
    private val firebaseAuthService: FirebaseAuthService =
        FirebaseAuthService(),
    private val firebaseProfileService: FirebaseProfileService =
        FirebaseProfileService()
) : ViewModel() {

    private val _loginForm = MutableStateFlow(LoginForm())
    val loginForm: StateFlow<LoginForm> = _loginForm.asStateFlow()

    private val _registerForm = MutableStateFlow(RegisterForm())
    val registerForm: StateFlow<RegisterForm> = _registerForm.asStateFlow()

    private val _forgotForm = MutableStateFlow(ForgotForm())
    val forgotForm: StateFlow<ForgotForm> = _forgotForm.asStateFlow()

    private val _event = MutableStateFlow<UiMessage?>(null)
    val event: StateFlow<UiMessage?> = _event.asStateFlow()

    fun consumeEvent() {
        _event.value = null
    }

    // ---- Connexion ----

    fun onLoginIdentifierChange(value: String) = _loginForm.update { it.copy(identifier = value) }
    fun onLoginPasswordChange(value: String) = _loginForm.update { it.copy(password = value) }

    fun login() {
        val form = _loginForm.value

        if (form.identifier.isBlank() || form.password.isBlank()) {
            _event.value = UiMessage.FILL_ALL_FIELDS
            return
        }

        viewModelScope.launch {
            val identifier = form.identifier.trim()

            // Recherche d'abord la copie locale Room.
            val localUser = repository.findByIdentifier(identifier)

            if (localUser == null) {
                _event.value = UiMessage.INVALID_CREDENTIALS
                return@launch
            }

            // Vérification locale nécessaire pour autoriser le mode hors ligne.
            val localPasswordValid = PasswordHasher.verify(
                password = form.password,
                salt = localUser.salt,
                expectedHash = localUser.passwordHash
            )

            if (!localPasswordValid) {
                _event.value = UiMessage.INVALID_CREDENTIALS
                return@launch
            }

            /*
             * Les anciens comptes locaux et les administrateurs qui n'ont pas
             * encore de firebaseUid continuent temporairement à fonctionner
             * avec Room.
             */
            if (localUser.firebaseUid.isNullOrBlank()) {
                sessionState.setCurrentUser(localUser.id)
                _event.value = UiMessage.LOGIN_SUCCESS
                return@launch
            }

            // Le compte possède un UID : tentative de connexion Firebase.
            when (
                val result = firebaseAuthService.signIn(
                    email = localUser.email,
                    password = form.password
                )
            ) {
                is AccountLoginResult.Success -> {
                    if (result.firebaseUid != localUser.firebaseUid) {
                        _event.value = UiMessage.INVALID_CREDENTIALS
                        return@launch
                    }

                    // Met à jour Firestore et retente une éventuelle ancienne sauvegarde.
                    firebaseProfileService.saveProfile(localUser)

                    sessionState.setCurrentUser(localUser.id)
                    _event.value = UiMessage.LOGIN_SUCCESS
                }

                AccountLoginResult.InternetUnavailable -> {
                    /*
                     * Firebase est inaccessible, mais le compte a déjà été
                     * validé et sa copie locale existe : connexion hors ligne.
                     */
                    sessionState.setCurrentUser(localUser.id)
                    _event.value = UiMessage.LOGIN_SUCCESS
                }

                AccountLoginResult.InvalidCredentials -> {
                    _event.value = UiMessage.INVALID_CREDENTIALS
                }

                AccountLoginResult.UnknownError -> {
                    _event.value = UiMessage.INVALID_CREDENTIALS
                }
            }
        }
    }

    // ---- Inscription ----

    fun onRegisterLastName(v: String) = _registerForm.update { it.copy(lastName = v) }
    fun onRegisterFirstName(v: String) = _registerForm.update { it.copy(firstName = v) }
    fun onRegisterBirth(v: Long) = _registerForm.update { it.copy(birthDateEpochDay = v) }
    fun onRegisterCin(v: String) = _registerForm.update { it.copy(cinNumber = v) }
    fun onRegisterProfession(v: String) = _registerForm.update { it.copy(profession = v) }
    fun onRegisterEmail(v: String) = _registerForm.update { it.copy(email = v) }
    fun onRegisterPassword(v: String) = _registerForm.update { it.copy(password = v) }
    fun onRegisterConfirm(v: String) = _registerForm.update { it.copy(confirmPassword = v) }
    fun onRegisterPolicyChange(accepted: Boolean) = _registerForm.update { it.copy(acceptedPolicy = accepted) }

    fun register() {
        val form = _registerForm.value

        val required = listOf(
            form.lastName.trim(),
            form.firstName.trim(),
            form.cinNumber.trim(),
            form.profession.trim(),
            form.email.trim(),
            form.password,
            form.confirmPassword
        )

        if (required.any { it.isEmpty() } || form.birthDateEpochDay == null) {
            _event.value = UiMessage.FILL_ALL_FIELDS
            return
        }

        if (!form.acceptedPolicy) {
            _event.value = UiMessage.ACCEPT_POLICY_REQUIRED
            return
        }

        val email = form.email.trim().lowercase()

        if (
            !email.contains("@") ||
            email.indexOf('@') == 0 ||
            email.indexOf('@') == email.length - 1
        ) {
            _event.value = UiMessage.INVALID_EMAIL
            return
        }

        if (form.password.length < 6) {
            _event.value = UiMessage.PASSWORD_SHORT
            return
        }

        if (form.password != form.confirmPassword) {
            _event.value = UiMessage.PASSWORD_MISMATCH
            return
        }

        viewModelScope.launch {
            // Vérification locale avant la création Firebase.
            if (repository.findUserByEmail(email) != null) {
                _event.value = UiMessage.EMAIL_TAKEN
                return@launch
            }

            when (
                val firebaseResult = firebaseAuthService.createAccount(
                    email = email,
                    password = form.password
                )
            ) {
                is AccountCreationResult.Success -> {
                    try {
                        val rank = repository.countMembers() + 1
                        val salt = PasswordHasher.newSalt()

                        val user = UserAccount(
                            lastName = form.lastName.trim(),
                            firstName = form.firstName.trim(),
                            birthDateEpochDay = form.birthDateEpochDay!!,
                            cinNumber = form.cinNumber.trim(),
                            profession = form.profession.trim(),
                            email = email,
                            firebaseUid = firebaseResult.firebaseUid,
                            passwordHash = PasswordHasher.hash(
                                form.password,
                                salt
                            ),
                            salt = salt,
                            username = AccountRules.usernameFor(
                                form.lastName.trim(),
                                rank
                            ),
                            registrationRank = rank,
                            language = "fr",
                            monthlyContribution = 100_000
                        )

                        // Copie locale utilisée pour le fonctionnement hors ligne.
                        val localUserId = repository.registerUser(user)

                        // Sauvegarde en ligne. En cas d'échec, le compte local reste utilisable.
                        firebaseProfileService.saveProfile(user)

                        sessionState.setCurrentUser(localUserId)
                        _registerForm.value = RegisterForm()
                    } catch (_: Exception) {
                        _event.value =
                            UiMessage.ONLINE_REGISTRATION_FAILED
                    }
                }

                AccountCreationResult.EmailAlreadyUsed -> {
                    _event.value = UiMessage.EMAIL_TAKEN
                }

                AccountCreationResult.InternetRequired -> {
                    _event.value = UiMessage.INTERNET_REQUIRED
                }

                AccountCreationResult.UnknownError -> {
                    _event.value =
                        UiMessage.ONLINE_REGISTRATION_FAILED
                }
            }
        }
    }

    // ---- Mot de passe oublié ----

    fun onForgotEmail(v: String) = _forgotForm.update { it.copy(email = v) }
    fun onForgotCin(v: String) = _forgotForm.update { it.copy(cinNumber = v) }
    fun onForgotNewPassword(v: String) = _forgotForm.update { it.copy(newPassword = v) }
    fun onForgotConfirm(v: String) = _forgotForm.update { it.copy(confirmPassword = v) }

    fun resetPassword() {
        val form = _forgotForm.value
        if (form.email.isBlank() || form.cinNumber.isBlank() || form.newPassword.isBlank() || form.confirmPassword.isBlank()) {
            _event.value = UiMessage.FILL_ALL_FIELDS
            return
        }
        if (form.newPassword.length < 6) {
            _event.value = UiMessage.PASSWORD_SHORT
            return
        }
        if (form.newPassword != form.confirmPassword) {
            _event.value = UiMessage.PASSWORD_MISMATCH
            return
        }
        viewModelScope.launch {
            val user = repository.findUserByEmail(form.email.trim())
            if (user == null) {
                _event.value = UiMessage.ACCOUNT_NOT_FOUND
                return@launch
            }
            if (user.cinNumber != form.cinNumber.trim()) {
                _event.value = UiMessage.CIN_MISMATCH
                return@launch
            }
            val salt = PasswordHasher.newSalt()
            repository.updateUser(
                user.copy(
                    salt = salt,
                    passwordHash = PasswordHasher.hash(form.newPassword, salt)
                )
            )
            _forgotForm.value = ForgotForm()
            _event.value = UiMessage.RESET_SUCCESS
        }
    }

    fun todayEpochDay(): Long = LocalDate.now().toEpochDay()

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as HoavikoApplication
                AuthViewModel(app.repository, app.session)
            }
        }
    }
}