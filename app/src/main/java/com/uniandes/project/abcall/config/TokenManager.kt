package com.uniandes.project.abcall.config

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.uniandes.project.abcall.models.Auth

class PreferencesManager(private val sharedPreferences: SharedPreferences) {


    companion object {
        const val AUTH_KEY = "authKey"
        const val TOKEN = "token"
        private val gson = Gson()
    }

    // Método para guardar el objeto Auth en SharedPreferences
    fun saveAuth(auth: Auth) {
        val preference = gson.toJson(auth) // Convierte el objeto a JSON
        val editor = sharedPreferences.edit()
        editor.putString(AUTH_KEY, preference) // Guarda el JSON
        editor.apply() // Aplica los cambios
    }

    fun getAuth(): Principal {
        val authJson = sharedPreferences.getString(AUTH_KEY, null)
        return gson.fromJson(authJson, Principal::class.java)
    }

    // Método para eliminar el token
    fun deleteToken() {
        val editor = sharedPreferences.edit()
        editor.remove(AUTH_KEY) // Elimina el token
        editor.apply() // Aplica los cambios
    }

    fun saveToken(token: String){
        val editor = sharedPreferences.edit()
        editor.putString(TOKEN, token)
        editor.apply()
    }

    fun getToken() : String? {
        return sharedPreferences.getString(TOKEN, null)
    }

    fun deleteToken() {
        val editor = sharedPreferences.edit()
        editor.remove(TOKEN) // Elimina el token
        editor.apply()
    }
}