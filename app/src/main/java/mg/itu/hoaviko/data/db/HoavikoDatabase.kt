package mg.itu.hoaviko.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import mg.itu.hoaviko.data.dao.ContributionDao
import mg.itu.hoaviko.data.dao.PaymentChannelDao
import mg.itu.hoaviko.data.dao.UserAccountDao
import mg.itu.hoaviko.data.dao.WithdrawalDao
import mg.itu.hoaviko.data.entity.Contribution
import mg.itu.hoaviko.data.entity.PaymentChannel
import mg.itu.hoaviko.data.entity.UserAccount
import mg.itu.hoaviko.data.entity.Withdrawal
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
@Database(
    entities = [UserAccount::class, PaymentChannel::class, Contribution::class, Withdrawal::class],
    version = 6,
    exportSchema = false
)
abstract class HoavikoDatabase : RoomDatabase() {

    abstract fun userAccountDao(): UserAccountDao
    abstract fun paymentChannelDao(): PaymentChannelDao
    abstract fun contributionDao(): ContributionDao
    abstract fun withdrawalDao(): WithdrawalDao

    companion object {

        @Volatile
        private var instance: HoavikoDatabase? = null
        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE users ADD COLUMN firebaseUid TEXT DEFAULT NULL"
                )
            }
        }

        fun get(context: Context): HoavikoDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    HoavikoDatabase::class.java,
                    "hoaviko.db"
                )
                    .addMigrations(MIGRATION_5_6)
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { instance = it }
            }
    }
}