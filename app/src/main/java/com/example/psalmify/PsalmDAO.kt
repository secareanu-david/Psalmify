package com.example.psalmify

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PsalmDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(psalm: Psalm)

    @Query("SELECT * FROM psalm_table WHERE id = :psalmId")
    suspend fun getPsalm(psalmId: Int): Psalm?

    @Query("SELECT * FROM psalm_table")
    fun getAllPsalms(): Flow<List<Psalm>>

    @Query("SELECT * FROM psalm_table WHERE id IN (:favoritePsalms)")
    fun getFavoritePsalms(favoritePsalms : List<Int>): Flow<List<Psalm>>
}
