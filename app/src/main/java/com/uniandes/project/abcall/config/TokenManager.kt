package com.uniandes.project.abcall.config

import androidx.security.crypto.EncryptedSharedPreferences
import android.content.Context
import androidx.security.crypto.MasterKey
import com.google.gson.Gson
import com.uniandes.project.abcall.models.Auth

class TokenManager(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "ABCAllPreferences",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        private const val AUTH_KEY = "authKey"
        private val gson = Gson()
    }

    fun saveAuth(auth: Auth) {
        val preference = gson.toJson(auth)
        val editor = sharedPreferences.edit()
        editor.putString(AUTH_KEY, preference)
        editor.apply()
    }

    fun getAuth(): Auth? {
        val authJson = sharedPreferences.getString(AUTH_KEY, null) ?: return null
        return gson.fromJson(authJson, Auth::class.java)
    }

    fun deleteToken() {
        val editor = sharedPreferences.edit()
        editor.remove(AUTH_KEY)
        editor.apply()
    }

}