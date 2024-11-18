package com.uniandes.project.abcall

import android.content.Context
import android.content.SharedPreferences
import com.uniandes.project.abcall.enums.KeySharedPreferences
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun getCustomSharedPreferences(context: Context) : SharedPreferences {
    return context.getSharedPreferences (
        KeySharedPreferences.ABCALL_PREFERENCES.name,
        Context.MODE_PRIVATE
    )
}

fun getDateStringFromCalendar(calendar: Calendar): String {
    val serverDateFormat = SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss 'GMT'Z", Locale.ENGLISH)
    val formattedDate = serverDateFormat.format(calendar.time)
    val outputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)

    return try {
        val parsedDate = serverDateFormat.parse(formattedDate)
        outputDateFormat.format(parsedDate!!)
    } catch (e: Exception) {
        println("Error al convertir la fecha: ${e.message}")
        ""
    }
}