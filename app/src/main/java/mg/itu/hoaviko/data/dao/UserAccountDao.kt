package mg.itu.hoaviko.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Index
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import mg.itu.hoaviko.data.entity.UserAccount

@Dao
interface UserAccountDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: UserAccount): Long

    @Update
    suspend fun update(user: UserAccount)

    @Query("SELECT COUNT(*) FROM users")
    suspend fun count(): Int

    @Query("SELECT COUNT(*) FROM users WHERE role = 'USER'")
    suspend fun countMembers(): Int

    @Query("SELECT COUNT(*) FROM users WHERE role = 'USER'")
    fun observeMemberCount(): kotlinx.coroutines.flow.Flow<Int>

    @Query("SELECT * FROM users WHERE id = :id")
    fun observeById(id: Long): kotlinx.coroutines.flow.Flow<UserAccount?>

@Query("SELECT * FROM users WHERE id = :id")
    suspend fun getById(id: Long): UserAccount?

    @Query("SELECT * FROM users ORDER BY registrationRank ASC")
    fun observeAll(): kotlinx.coroutines.flow.Flow<List<UserAccount>>

    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun findByEmail(email: String): UserAccount?


    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun findByUsername(username: String): UserAccount?

    @Query("SELECT * FROM users WHERE email = :identifier OR username = :identifier LIMIT 1")
    suspend fun findByIdentifier(identifier: String): UserAccount?
}