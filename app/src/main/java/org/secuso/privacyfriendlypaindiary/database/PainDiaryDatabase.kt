package org.secuso.privacyfriendlypaindiary.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import org.secuso.privacyfriendlybackup.api.backup.DatabaseUtil
import org.secuso.privacyfriendlypaindiary.database.dao.*
import org.secuso.privacyfriendlypaindiary.database.model.*
import org.secuso.privacyfriendlypaindiary.database.utils.Converters

@Database(
    entities = [PainDescription::class, Drug::class, DrugIntake::class, DiaryEntry::class, User::class],
    version = PainDiaryDatabase.VERSION
)
@TypeConverters(Converters::class)
abstract class PainDiaryDatabase : RoomDatabase() {
    abstract fun painDescriptionDao(): PainDescriptionDao
    abstract fun drugDao(): DrugDao
    abstract fun drugIntakeDao(): DrugIntakeDao
    abstract fun diaryEntryDao(): DiaryEntryDao
    abstract fun userDao(): UserDao

    companion object {
        const val DATABASE_NAME = "paindiary"
        const val VERSION = 2
        private var instance: PainDiaryDatabase? = null

        fun getInstance(context: Context): PainDiaryDatabase {
            synchronized(DATABASE_NAME) {
                if (instance == null) {
                    instance = createDatabase(context)
                }
                return instance!!
            }
        }

        fun resetDatabase(context: Context) {
            synchronized(DATABASE_NAME) {
                DatabaseUtil.deleteRoomDatabase(context, DATABASE_NAME)
                instance = null
            }
        }

        private fun createDatabase(context: Context): PainDiaryDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                PainDiaryDatabase::class.java, DATABASE_NAME
            )
                .addMigrations(MIGRATION_1_2)
                .addCallback(roomCallback)
                .build()
        }

        private val roomCallback: Callback = object: Callback() {}

//            object : Callback() {
//            override fun onCreate(db: SupportSQLiteDatabase) {
//                super.onCreate(db)
//            }
//        }


        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE `diaryentries` (`_id` INTEGER NOT NULL," +
                            "`date` TEXT UNIQUE NOT NULL," +
                            "`painDescription_id` INTEGER NOT NULL," +
                            "`condition` INTEGER," +
                            "`notes` TEXT," +
                            "PRIMARY KEY(`_id`))"
                )
                database.execSQL(
                    "INSERT INTO `diaryentries`(_id, date, painDescription_id, condition, notes)" +
                            " SELECT id, date, paindescription_id, condition, notes FROM diaryentry"
                )
                database.execSQL("DROP TABLE diaryentry")


                database.execSQL(
                    "CREATE TABLE `drugs` (`_id` INTEGER NOT NULL," +
                            "`name` TEXT NOT NULL," +
                            "`dose` TEXT," +
                            "PRIMARY KEY(`_id`))"
                )
                database.execSQL(
                    "INSERT INTO `drugs`(_id, name, dose)" +
                            " SELECT id, name, dose FROM drug"
                )
                database.execSQL("DROP TABLE drug")


                database.execSQL(
                    "CREATE TABLE `drugintakes` (`_id` INTEGER NOT NULL," +
                            "`morning` INTEGER NOT NULL DEFAULT 0," +
                            "`noon` INTEGER NOT NULL DEFAULT 0," +
                            "`evening` INTEGER NOT NULL DEFAULT 0," +
                            "`night` INTEGER NOT NULL DEFAULT 0," +
                            "`drug_id` INTEGER NOT NULL," +
                            "`diaryEntry_id` INTEGER NOT NULL," +
                            "PRIMARY KEY(`_id`))"
                )
                database.execSQL(
                    "INSERT INTO `drugintakes`(_id, morning, noon, evening, night, drug_id, diaryEntry_id)" +
                            " SELECT id, morning, noon, evening, night, drug_id, diaryentry_id FROM drugintake"
                )
                database.execSQL("DROP TABLE drugintake")


                database.execSQL(
                    "CREATE TABLE `paindescriptions` (`_id` INTEGER NOT NULL," +
                            "`painLevel` INTEGER NOT NULL," +
                            "`bodyRegions` TEXT," +
                            "`painQualities` TEXT," +
                            "`timesOfPain` TEXT," +
                            "PRIMARY KEY(`_id`))"
                )
                database.execSQL(
                    "INSERT INTO `paindescriptions`(_id, painLevel, bodyRegions, painQualities, timesOfPain)" +
                            " SELECT id, painLevel, bodyRegions, painQualities, timesOfPain FROM paindescription"
                )
                database.execSQL("DROP TABLE paindescription")


                database.execSQL(
                    "CREATE TABLE `users` (`_id` INTEGER NOT NULL," +
                            "`firstname` TEXT," +
                            "`lastname` TEXT," +
                            "`gender` INTEGER," +
                            "`dateOfBirth` TEXT," +
                            "PRIMARY KEY(`_id`))"
                )
                database.execSQL(
                    "INSERT INTO `users`(_id, firstname, lastname, gender, dateOfBirth)" +
                            " SELECT id, firstname, lastname, gender, dateOfBirth FROM user"
                )
                database.execSQL("DROP TABLE user")
            }
        }
    }
}