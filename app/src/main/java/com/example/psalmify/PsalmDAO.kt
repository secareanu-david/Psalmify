package com.example.psalmify

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PsalmDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(psalm: Psalm)

    @Query("SELECT * FROM psalm_table WHERE id = :psalmId")
    suspend fun getPsalm(psalmId: Int): Psalm?

    @Query("SELECT * FROM psalm_table")
    suspend fun getAllPsalms(): List<Psalm>
}
