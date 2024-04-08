package org.secuso.privacyfriendlypaindiary.database.dao

import androidx.room.*
import org.secuso.privacyfriendlypaindiary.database.model.User

@Dao
interface UserDao {
    @Insert
    fun insert(user: User): Long

    @Update
    fun update(user: User)

    @Delete
    fun delete(user: User)

    @Query("SELECT * FROM users")
    fun loadAllUsers(): Array<User>

    @Query("DELETE FROM users WHERE _id = :id")
    fun deleteUserByID(id: Long)

    @Query("SELECT * FROM users WHERE _id = :id")
    fun loadUserByID(id: Long): User?
}