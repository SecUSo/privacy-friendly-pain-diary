package org.secuso.privacyfriendlypaindiary.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import org.secuso.privacyfriendlypaindiary.database.dao.*
import org.secuso.privacyfriendlypaindiary.database.model.*

@Database(
    entities = [PainDescription::class, Drug::class, DrugIntake::class, DiaryEntry::class, User::class],
    version = 2
)
abstract class PainDiaryDatabase : RoomDatabase() {
    abstract fun painDescriptionDao(): PainDescriptionDao
    abstract fun drugDao(): DrugDao
    abstract fun drugIntakeDao(): DrugIntakeDao
    abstract fun diaryEntryDao(): DiaryEntryDao
    abstract fun userDao(): UserDao

    companion object {
        private var instance: PainDiaryDatabase? = null

        @Synchronized
        open fun getInstance(context: Context): PainDiaryDatabase? {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    PainDiaryDatabase::class.java, "paindiaryroom"
                )
                    //.addMigrations(MIGRATION_1_2)
                    .addCallback(roomCallback)
                    .build()
            }
            return instance
        }

        private val roomCallback: Callback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
            }
        }


        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // TODO: add migration
            }
        }
    }
}