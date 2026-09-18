package mg.itu.hoaviko.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

/** Persiste l'identifiant de la session utilisateur active. */
class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var currentUserId: Long
        get() = prefs.getLong(KEY_USER_ID, NO_USER)
        set(value) = prefs.edit { putLong(KEY_USER_ID, value) }

    fun logout() = prefs.edit { remove(KEY_USER_ID) }

    companion object {
        private const val PREFS_NAME = "hoaviko_session"
        private const val KEY_USER_ID = "current_user_id"
        const val NO_USER: Long = -1L
    }
}