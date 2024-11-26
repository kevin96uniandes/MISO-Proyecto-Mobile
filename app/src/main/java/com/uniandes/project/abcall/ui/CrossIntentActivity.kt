package com.uniandes.project.abcall.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

abstract class CrossIntentActivity : AppCompatActivity() {fun nextActivity(activity: Class<*>, flags: Int? = null, extras: List<Pair<String, String>>? = null) {
    val intent = Intent(this, activity)
    flags?.let {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
    }

    extras?.let{
        for ((key, value) in extras) {
            println("$key = $value")
            intent.putExtra(key, value)
        }
    }

    startActivity(intent)
}



}