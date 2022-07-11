package org.secuso.privacyfriendlypaindiary.database.utils

import android.util.Log
import androidx.room.TypeConverter
import org.secuso.privacyfriendlypaindiary.database.DBService
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object Converters {
    private val formatter = SimpleDateFormat(DBService.DATE_PATTERN, Locale.ENGLISH)

    @TypeConverter
    @JvmStatic
    fun fromDate(date: Date?): String? {
        return date?.let { formatter.format(it) }
    }

    @TypeConverter
    @JvmStatic
    fun toDate(date: String?): Date? {
        if (date == null) return null
        return try {
            formatter.parse(date)
        } catch (e: ParseException) {
            Log.e(Converters.javaClass.simpleName, e.message, e)
            return null
        }
    }
}