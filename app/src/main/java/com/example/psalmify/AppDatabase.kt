package com.example.psalmify

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.io.InputStream

@Database(entities = [User::class, Psalm::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun psalmDao(): PsalmDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .addCallback(AppDatabaseCallback(context, scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
        private class AppDatabaseCallback(
            private val context: Context,
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.psalmDao(), context)
                    }
                }
            }
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                // Uncomment this line if you want to populate the database every time it's opened
                // This might not be necessary if the database doesn't change frequently
                 //INSTANCE?.let { database ->
                  //  scope.launch(Dispatchers.IO) {
                  //      populateDatabase(database.psalmDao(), context)
                  //  }
                // }
            }
        }

       public suspend fun populateDatabase(psalmDao: PsalmDao, context: Context) {
            val psalms = readPsalmsFromJson(context)
            psalms.forEach { psalm ->
                psalmDao.insert(psalm)
            }
        }
        private fun readPsalmsFromJson(context: Context): List<Psalm> {
            val inputStream: InputStream = context.resources.openRawResource(R.raw.psalms)
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            return Json.decodeFromString(jsonString)
        }
    }
}
