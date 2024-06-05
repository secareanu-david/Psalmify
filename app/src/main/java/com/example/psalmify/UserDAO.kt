package com.example.psalmify

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Query("SELECT * FROM user_table WHERE id = :userId")
    suspend fun getUser(userId: String): User?

    @Query("SELECT * FROM user_table")
    suspend fun getAllUsers(): List<User>
}
