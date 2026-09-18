package mg.itu.hoaviko

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import mg.itu.hoaviko.data.HoavikoRepository
import mg.itu.hoaviko.data.SessionManager
import mg.itu.hoaviko.data.db.HoavikoDatabase
import mg.itu.hoaviko.presentation.viewmodel.SessionState

class HoavikoApplication : Application() {

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val database: HoavikoDatabase by lazy { HoavikoDatabase.get(this) }
    val repository: HoavikoRepository by lazy { HoavikoRepository(database) }
    val session: SessionState by lazy { SessionState(repository, SessionManager(this)) }

    override fun onCreate() {
        super.onCreate()
        appScope.launch {
            repository.seedChannelsIfEmpty()
            repository.seedAdminsIfMissing()
        }
    }
}