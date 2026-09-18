package mg.itu.hoaviko.data.remote

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.tasks.await

/**
 * Résultats possibles lors de la création d'un compte Firebase.
 */
sealed interface AccountCreationResult {

    data class Success(
        val firebaseUid: String
    ) : AccountCreationResult

    data object EmailAlreadyUsed : AccountCreationResult

    data object InternetRequired : AccountCreationResult

    data object UnknownError : AccountCreationResult
}

/**
 * Résultats possibles lors d'une connexion Firebase.
 */
sealed interface AccountLoginResult {

    data class Success(
        val firebaseUid: String
    ) : AccountLoginResult

    data object InvalidCredentials : AccountLoginResult

    data object InternetUnavailable : AccountLoginResult

    data object UnknownError : AccountLoginResult
}

/**
 * Service responsable de l'authentification Firebase.
 *
 * Room reste responsable de la copie locale du compte afin de permettre
 * l'utilisation de l'application hors ligne.
 */
class FirebaseAuthService(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    /**
     * Crée un nouveau compte dans Firebase Auth.
     *
     * La création d'un nouveau compte nécessite Internet.
     */
    suspend fun createAccount(
        email: String,
        password: String
    ): AccountCreationResult {
        return try {
            val result = auth
                .createUserWithEmailAndPassword(
                    email.trim().lowercase(),
                    password
                )
                .await()

            val uid = result.user?.uid
                ?: return AccountCreationResult.UnknownError

            AccountCreationResult.Success(
                firebaseUid = uid
            )
        } catch (_: FirebaseAuthUserCollisionException) {
            AccountCreationResult.EmailAlreadyUsed
        } catch (_: FirebaseNetworkException) {
            AccountCreationResult.InternetRequired
        } catch (_: Exception) {
            AccountCreationResult.UnknownError
        }
    }

    /**
     * Connecte l'utilisateur à Firebase lorsque le réseau est disponible.
     */
    suspend fun signIn(
        email: String,
        password: String
    ): AccountLoginResult {
        return try {
            val result = auth
                .signInWithEmailAndPassword(
                    email.trim().lowercase(),
                    password
                )
                .await()

            val uid = result.user?.uid
                ?: return AccountLoginResult.UnknownError

            AccountLoginResult.Success(
                firebaseUid = uid
            )
        } catch (_: FirebaseNetworkException) {
            AccountLoginResult.InternetUnavailable
        } catch (_: FirebaseAuthInvalidCredentialsException) {
            AccountLoginResult.InvalidCredentials
        } catch (_: FirebaseAuthInvalidUserException) {
            AccountLoginResult.InvalidCredentials
        } catch (_: Exception) {
            AccountLoginResult.UnknownError
        }
    }

    /**
     * Retourne l'identifiant Firebase de l'utilisateur connecté.
     */
    fun currentFirebaseUid(): String? {
        return auth.currentUser?.uid
    }

    /**
     * Indique si une session Firebase existe.
     */
    fun isSignedIn(): Boolean {
        return auth.currentUser != null
    }

    /**
     * Déconnecte la session Firebase.
     *
     * La suppression de la session locale Room sera gérée séparément
     * par SessionState.
     */
    fun signOut() {
        auth.signOut()
    }
}