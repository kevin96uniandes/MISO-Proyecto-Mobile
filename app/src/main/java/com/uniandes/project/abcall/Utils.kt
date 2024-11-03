package com.uniandes.project.abcall

import android.content.Context
import android.content.SharedPreferences
import com.uniandes.project.abcall.enums.KeySharedPreferences

fun getCustomSharedPreferences(context: Context) : SharedPreferences {
    return context.getSharedPreferences (
        KeySharedPreferences.ABCALL_PREFERENCES.name,
        Context.MODE_PRIVATE
    )
}