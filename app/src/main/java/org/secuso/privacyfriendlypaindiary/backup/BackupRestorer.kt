package org.secuso.privacyfriendlypaindiary.backup

import android.content.Context
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.preference.PreferenceManager
import android.util.JsonReader
import androidx.annotation.NonNull
import org.secuso.privacyfriendlypaindiary.database.PainDiaryDatabase.Companion.DATABASE_NAME
import org.secuso.privacyfriendlybackup.api.backup.DatabaseUtil
import org.secuso.privacyfriendlybackup.api.backup.DatabaseUtil.readDatabaseContent
import org.secuso.privacyfriendlybackup.api.backup.FileUtil
import org.secuso.privacyfriendlybackup.api.pfa.IBackupRestorer
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

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
        val db: SQLiteDatabase =
            SQLiteDatabase.openOrCreateDatabase(context.getDatabasePath("restoreDatabase"), null)
        db.beginTransaction()
        db.setVersion(version)
        readDatabaseContent(reader, db)
        db.setTransactionSuccessful()
        db.endTransaction()
        db.close()
        reader.endObject()

        // copy file to correct location
        val databaseFile: File = context.getDatabasePath("restoreDatabase")
        DatabaseUtil.deleteRoomDatabase(context, DATABASE_NAME)
        FileUtil.copyFile(databaseFile, context.getDatabasePath(DATABASE_NAME))
        databaseFile.delete()
    }

    @Throws(IOException::class)
    private fun readPreferences(@NonNull reader: JsonReader, @NonNull context: Context) {
        reader.beginObject()
        val pref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        while (reader.hasNext()) {
            val name: String = reader.nextName()
            when (name) {
                "pref_medication", "pref_reminder" -> pref.edit()
                    .putBoolean(name, reader.nextBoolean()).apply()
                "pref_reminder_time" -> pref.edit().putLong(name, reader.nextLong()).apply()
                else -> throw RuntimeException("Unknown preference $name")
            }
        }
        reader.endObject()
    }

    override fun restoreBackup(context: Context, restoreData: InputStream): Boolean {
        return try {
            val isReader = InputStreamReader(restoreData)
            val reader = JsonReader(isReader)

            // START
            reader.beginObject()
            while (reader.hasNext()) {
                val type: String = reader.nextName()
                when (type) {
                    "database" -> readDatabase(reader, context)
                    "preferences" -> readPreferences(reader, context)
                    else -> throw RuntimeException("Can not parse type $type")
                }
            }
            reader.endObject()
            true
        } catch (e: Exception) {
            false
        }
    }
}