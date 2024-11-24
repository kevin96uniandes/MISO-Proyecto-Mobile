package com.uniandes.project.abcall.ui

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.uniandes.project.abcall.R
import com.uniandes.project.abcall.ui.dashboard.DashboardActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginActivityTest {


    @Test
    fun testLoginButtonClick_withEmptyFields() {
        val intent = Intent(ApplicationProvider.getApplicationContext(), LoginActivity::class.java).apply {
            putExtra("isTest", true) // Pasa el flag "isTest" a la actividad
        }
        ActivityScenario.launch<LoginActivity>(intent)
        // Simula un clic en el botón de login con campos vacíos
        onView(withId(R.id.btn_log_in)).perform(click())

        // Verifica que se muestren los errores en los campos
        onView(withId(R.id.ilUsername)).check(matches(hasDescendant(withText("Por favor ingresa tu nombre de usuario"))))
        onView(withId(R.id.ilPassword)).check(matches(hasDescendant(withText("Por favor ingresa tu contraseña"))))
    }

    @Test
    fun testLoginButtonClick_withInvalidCredentials() {
        // Escribe un nombre de usuario y contraseña inválidos
        onView(withId(R.id.et_username)).perform(typeText("usuario_invalido"), closeSoftKeyboard())
        onView(withId(R.id.et_password)).perform(typeText("password_invalido"), closeSoftKeyboard())

        // Simula el clic en el botón de login
        onView(withId(R.id.btn_log_in)).perform(click())

        // Verifica que se muestre el mensaje de error
        onView(withText("Usuario y/o contraseña incorrecta"))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
    }

    @Test
    fun testLoginButtonClick_withValidCredentials() {
        // Escribe un nombre de usuario y contraseña válidos
        onView(withId(R.id.et_username)).perform(typeText("usuario_valido"), closeSoftKeyboard())
        onView(withId(R.id.et_password)).perform(typeText("password_valido"), closeSoftKeyboard())

        // Simula el clic en el botón de login
        onView(withId(R.id.btn_log_in)).perform(click())

        // Verifica que se cambie a la siguiente actividad (DashboardActivity)
        intended(hasComponent(DashboardActivity::class.java.name))
    }
}
