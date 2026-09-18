package mg.itu.hoaviko.data

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import mg.itu.hoaviko.data.dao.ContributionDao
import mg.itu.hoaviko.data.dao.PaymentChannelDao
import mg.itu.hoaviko.data.dao.UserAccountDao
import mg.itu.hoaviko.data.dao.WithdrawalDao
import mg.itu.hoaviko.data.dao.WithdrawalWithUser
import mg.itu.hoaviko.data.db.HoavikoDatabase
import mg.itu.hoaviko.data.entity.Contribution
import mg.itu.hoaviko.data.entity.PaymentChannel
import mg.itu.hoaviko.data.entity.UserAccount
import mg.itu.hoaviko.data.entity.UserAccount.Companion.ROLE_ADMIN
import mg.itu.hoaviko.data.entity.Withdrawal
import mg.itu.hoaviko.util.PasswordHasher

class HoavikoRepository(private val database: HoavikoDatabase) {

    private val userDao: UserAccountDao = database.userAccountDao()
    private val channelDao: PaymentChannelDao = database.paymentChannelDao()
    private val contributionDao: ContributionDao = database.contributionDao()
    private val withdrawalDao: WithdrawalDao = database.withdrawalDao()

    // ---- Canaux de paiement ----

    val channels: Flow<List<PaymentChannel>> = channelDao.getAll()

    suspend fun seedChannelsIfEmpty() {
        if (channelDao.count() == 0) channelDao.upsertAll(SeedData.channels)
    }

    suspend fun channelById(id: Long): PaymentChannel? = channelDao.getById(id)

    suspend fun getAllChannels(): List<PaymentChannel> = channelDao.getAllSuspend()

    // ---- Comptes utilisateurs ----

    suspend fun registerUser(user: UserAccount): Long = userDao.insert(user)

    suspend fun countUsers(): Int = userDao.count()

    suspend fun countMembers(): Int = userDao.countMembers()

    /** Nombre de comptes membres (rôle USER), observé en continu. */
    fun observeMemberCount(): Flow<Int> = userDao.observeMemberCount()

    /** Nombre de demandes de retrait reçues, observé en continu. */
    fun observeWithdrawalCount(): Flow<Int> = withdrawalDao.observeCount()

    fun observeAllUsers(): Flow<List<UserAccount>> = userDao.observeAll()

    /** Supprime un compte et tout son historique (versements, demandes de retrait). */
    suspend fun deleteUser(id: Long) = database.withTransaction {
        contributionDao.deleteByUser(id)
        withdrawalDao.deleteByUser(id)
        userDao.deleteById(id)
    }

    suspend fun findUserByEmail(email: String): UserAccount? = userDao.findByEmail(email)

    suspend fun findUserByUsername(username: String): UserAccount? = userDao.findByUsername(username)

    suspend fun findByIdentifier(identifier: String): UserAccount? = userDao.findByIdentifier(identifier)

    fun observeUser(id: Long): Flow<UserAccount?> = userDao.observeById(id)

    suspend fun updateUser(user: UserAccount) = userDao.update(user)

    suspend fun getUser(id: Long): UserAccount? = userDao.getById(id)

    // ---- Versements (par utilisateur) ----

    fun observeContributions(userId: Long): Flow<List<Contribution>> =
        contributionDao.observeByUser(userId)

    fun observeTotal(userId: Long): Flow<Long> = contributionDao.observeTotal(userId)

    fun observeCount(userId: Long): Flow<Int> = contributionDao.observeCount(userId)

    suspend fun addContribution(contribution: Contribution): Long =
        contributionDao.insert(contribution)

    // ---- Comptes administrateurs (créés au démarrage) ----

    suspend fun seedAdminsIfMissing() {
        if (userDao.findByEmail(ADMIN_EMAIL_FANASINA) == null) {
            val salt = PasswordHasher.newSalt()
            userDao.insert(
                UserAccount(
                    lastName = "Filamatra",
                    firstName = "Fanasina",
                    birthDateEpochDay = 0L,
                    cinNumber = "ADMIN-FANASINA",
                    profession = "Administrateur",
                    email = ADMIN_EMAIL_FANASINA,
                    passwordHash = PasswordHasher.hash(ADMIN_PASSWORD_FANASINA, salt),
                    salt = salt,
                    username = ADMIN_USERNAME_FANASINA,
                    registrationRank = 0,
                    role = ROLE_ADMIN
                )
            )
        }
        if (userDao.findByEmail(ADMIN_EMAIL_JASON) == null) {
            val salt = PasswordHasher.newSalt()
            userDao.insert(
                UserAccount(
                    lastName = "Fahasoavana",
                    firstName = "Jason",
                    birthDateEpochDay = 0L,
                    cinNumber = "ADMIN-JASON",
                    profession = "Administrateur",
                    email = ADMIN_EMAIL_JASON,
                    passwordHash = PasswordHasher.hash(ADMIN_PASSWORD_JASON, salt),
                    salt = salt,
                    username = ADMIN_USERNAME_JASON,
                    registrationRank = 0,
                    role = ROLE_ADMIN
                )
            )
        }
    }

    // ---- Demandes de retrait ----

    fun observeWithdrawals(userId: Long): Flow<List<Withdrawal>> =
        withdrawalDao.observeByUser(userId)

    fun observeAllWithdrawalsWithUser(): Flow<List<WithdrawalWithUser>> =
        withdrawalDao.observeAllWithUser()

    suspend fun addWithdrawal(withdrawal: Withdrawal): Long =
        withdrawalDao.insert(withdrawal)

    suspend fun updateWithdrawalStatus(id: Long, status: String) =
        withdrawalDao.updateStatus(id, status)

    companion object {
        const val ADMIN_USERNAME_FANASINA = "filamatra.0"
        const val ADMIN_EMAIL_FANASINA = "fanasinaadmin@hoaviko.mg"
        const val ADMIN_PASSWORD_FANASINA = "FanHoAvy.mg"
        const val ADMIN_USERNAME_JASON = "fahasoavana.0"
        const val ADMIN_EMAIL_JASON = "jasonadmin@hoaviko.mg"
        const val ADMIN_PASSWORD_JASON = "JasHoAvy.mg"
    }
}