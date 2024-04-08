package org.secuso.privacyfriendlypaindiary.backup

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.JsonReader
import androidx.annotation.NonNull
import org.secuso.privacyfriendlybackup.api.backup.DatabaseUtil
import org.secuso.privacyfriendlybackup.api.backup.DatabaseUtil.readDatabaseContent
import org.secuso.privacyfriendlybackup.api.backup.FileUtil
import org.secuso.privacyfriendlybackup.api.pfa.IBackupRestorer
import org.secuso.privacyfriendlypaindiary.database.PainDiaryDatabase.Companion.DATABASE_NAME
import org.secuso.privacyfriendlypaindiary.tutorial.PrefManager
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import kotlin.system.exitProcess

class BackupRestorer : IBackupRestorer {

    @Throws(IOException::class)
    private fun readDatabase(@NonNull reader: JsonReader, @NonNull context: Context) {
        reader.beginObject()
        val n1: String = reader.nextName()
        if (n1 != "version") {
            throw RuntimeException("Unknown value $n1")
        }
        val version: Int = reader.nextInt()
        val n2: String = reader.nextName()
        if (n2 != "content") {
            throw RuntimeException("Unknown value $n2")
        }
        val restoreDatabaseName = "restoreDatabase"
        val db = DatabaseUtil.getSupportSQLiteOpenHelper(
            context,
            restoreDatabaseName,
            version
        ).writableDatabase
        db.beginTransaction()
        db.version = version
        readDatabaseContent(reader, db)
        db.setTransactionSuccessful()
        db.endTransaction()
        db.close()
        reader.endObject()

        // copy file to correct location
        val databaseFile: File = context.getDatabasePath(restoreDatabaseName)
        DatabaseUtil.deleteRoomDatabase(context, DATABASE_NAME)
        FileUtil.copyFile(databaseFile, context.getDatabasePath(DATABASE_NAME))
        databaseFile.delete()
    }

    @Throws(IOException::class)
    private fun readPreferences(
        @NonNull reader: JsonReader,
        @NonNull prefEdit: SharedPreferences.Editor
    ) {
        reader.beginObject()
        while (reader.hasNext()) {
            val name: String = reader.nextName()
            when (name) {
                "pref_medication", "pref_reminder", "IsFirstTimeLaunch" -> prefEdit.putBoolean(
                    name,
                    reader.nextBoolean()
                )

                "pref_reminder_time", "userID" -> prefEdit.putLong(name, reader.nextLong())
                else -> throw RuntimeException("Unknown preference $name")
            }
        }
        reader.endObject()
    }

    override fun restoreBackup(context: Context, restoreData: InputStream): Boolean {
        return try {
            val isReader = InputStreamReader(restoreData)
            val reader = JsonReader(isReader)
            val pref = PreferenceManager.getDefaultSharedPreferences(context).edit()
            val pref2 =
                context.getSharedPreferences(PrefManager.PREF_NAME, PrefManager.PRIVATE_MODE).edit()

            // START
            reader.beginObject()
            while (reader.hasNext()) {
                val type: String = reader.nextName()
                when (type) {
                    "database" -> readDatabase(reader, context)
                    "preferences" -> readPreferences(
                        reader,
                        pref
                    )

                    "preferences2" -> readPreferences(
                        reader,
                        pref2
                    )

                    else -> throw RuntimeException("Can not parse type $type")
                }
            }
            reader.endObject()

            pref.commit()
            pref2.commit()

            exitProcess(0)
            true
        } catch (e: Exception) {
            false
        }
    }
}