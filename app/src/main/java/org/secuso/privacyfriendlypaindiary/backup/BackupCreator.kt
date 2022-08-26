package org.secuso.privacyfriendlypaindiary.backup

import android.content.Context
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.preference.PreferenceManager
import android.util.JsonWriter
import android.util.Log
import org.secuso.privacyfriendlybackup.api.backup.DatabaseUtil.getSupportSQLiteOpenHelper
import org.secuso.privacyfriendlybackup.api.backup.DatabaseUtil.writeDatabase
import org.secuso.privacyfriendlybackup.api.backup.PreferenceUtil.writePreferences
import org.secuso.privacyfriendlybackup.api.pfa.IBackupCreator
import org.secuso.privacyfriendlypaindiary.database.PainDiaryDatabase
import org.secuso.privacyfriendlypaindiary.tutorial.PrefManager
import java.io.OutputStream
import java.io.OutputStreamWriter
import kotlin.text.Charsets.UTF_8

class BackupCreator : IBackupCreator {
    override fun writeBackup(context: Context, outputStream: OutputStream) {
        Log.d("PFA BackupCreator", "createBackup() started")
        val outputStreamWriter = OutputStreamWriter(outputStream, UTF_8)
        val writer = JsonWriter(outputStreamWriter)
        writer.setIndent("")

        try {
            writer.beginObject()
            val dataBase = getSupportSQLiteOpenHelper(context, PainDiaryDatabase.DATABASE_NAME, PainDiaryDatabase.VERSION).readableDatabase
            Log.d("PFA BackupCreator", "Writing database")
            writer.name("database")
            writeDatabase(writer, dataBase)
            dataBase.close()
            Log.d("PFA BackupCreator", "Writing preferences")
            writer.name("preferences")
            val pref: SharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
            writePreferences(writer, pref)
            writer.name("preferences2")
            val prefTutorialActivity : SharedPreferences =
                context.getSharedPreferences(PrefManager.PREF_NAME, PrefManager.PRIVATE_MODE)
            writePreferences(writer, prefTutorialActivity)
            Log.d("PFA BackupCreator", "Writing files")
            writer.endObject()
            writer.close()
        } catch (e: Exception) {
            Log.e("PFA BackupCreator", "Error occurred", e)
            e.printStackTrace()
        }

        Log.d("PFA BackupCreator", "Backup created successfully")
    }
}