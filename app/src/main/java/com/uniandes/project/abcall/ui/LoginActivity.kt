package com.uniandes.project.abcall.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.uniandes.project.abcall.R
import com.uniandes.project.abcall.config.ApiResult
import com.uniandes.project.abcall.config.JwtManager
import com.uniandes.project.abcall.config.PreferencesManager
import com.uniandes.project.abcall.config.RetrofitClient
import com.uniandes.project.abcall.databinding.ActivityLoginBinding
import com.uniandes.project.abcall.enums.UserType
import com.uniandes.project.abcall.getCustomSharedPreferences
import com.uniandes.project.abcall.models.Principal
import com.uniandes.project.abcall.repositories.rest.AuthClient
import com.uniandes.project.abcall.ui.dashboard.DashboardActivity
import com.uniandes.project.abcall.ui.dialogs.CustomDialogFragment
import com.uniandes.project.abcall.viewmodels.AuthViewModel

class LoginActivity : CrossIntentActivity() {

    private lateinit var etUsername: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var ilUsername: TextInputLayout
    private lateinit var ilPassword: TextInputLayout
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: AuthViewModel
    private val authClient = AuthClient()
    //private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        //tokenManager = TokenManager(binding.root.context)

        viewModel = AuthViewModel(authClient)

        etUsername = findViewById(R.id.et_username)
        etPassword = findViewById(R.id.et_password)
        ilUsername = findViewById(R.id.ilUsername)
        ilPassword = findViewById(R.id.ilPassword)
        btnLogin = findViewById(R.id.btn_log_in)
        btnRegister = findViewById(R.id.btn_register)

        val sPreferences = getCustomSharedPreferences(binding.root.context)
        preferencesManager = PreferencesManager(sPreferences)

        sharedPreferences = sPreferences

        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        btnLogin.setOnClickListener { validateForm() }

        btnRegister.setOnClickListener {
            val intent = Intent(this, UserRegisterActivity::class.java)
            startActivity(intent)
        }

        if (isLoggedIn) {
            startActivity(
                Intent(this, DashboardActivity::class.java)
            )
            finish()
        }

        viewModel.result.observe(this) { result ->
            when (result) {
                is ApiResult.Success -> {
                    val token = result.data.token
                    preferencesManager.saveToken(token)
                    RetrofitClient.updateAuthToken(token)
                    jwtManager = JwtManager()
                    val claims = jwtManager.decodeJWT(token)
                    val principal = Principal(
                        id = claims["id"] as Int,
                        idCompany = claims["id_company"] as Int?,
                        idPerson = claims["id_person"] as Int?,
                        userType = UserType.fromString(claims["user_type"] as String)
                    )

                    preferencesManager.savePrincipal(principal)

                    with(sharedPreferences.edit()) {
                        putBoolean("isLoggedIn", true)
                        apply()
                    }
                    nextActivity (
                        DashboardActivity::class.java,
                        extras = listOf( Pair("token", token))
                    )
                    finish()
                }
                is ApiResult.Error -> {
                    val dialog = CustomDialogFragment().newInstance(
                        "Inicio de sesión",
                        "Usuario y/o contraseña incorrecta",
                        R.raw.error
                    )
                    dialog.show(supportFragmentManager, "CustomDialog")
                    Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
                }
                is ApiResult.NetworkError -> {
                    val dialog = CustomDialogFragment().newInstance(
                        "Inicio de sesión",
                        "No se pudo conectar con el servidor. Por favor, verifica tu conexión a internet.",
                        R.raw.no_network
                    )
                    dialog.show(supportFragmentManager, "CustomDialog")
                    Toast.makeText(this, "Error de red", Toast.LENGTH_SHORT).show()
                }
            }
        }

        setupTextWatchers()
    }

    private fun validateForm() {

        ilUsername.error = null
        ilPassword.error = null

        val username = etUsername.text.toString().trim()
        val password = etPassword.text.toString().trim()

        var isValid = true

        if (TextUtils.isEmpty(username)) {
            ilUsername.error = "Por favor ingresa tu nombre de usuario"
            isValid = false
        }

        if (TextUtils.isEmpty(password)) {
            ilPassword.error = "Por favor ingresa tu contraseña"
            isValid = false
        }

        if (isValid) {
            viewModel.authenticate(username, password)
        }
    }

    private fun setupTextWatchers() {
        // Configura el TextWatcher para el nombre de usuario
        etUsername.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                clearUsernameError()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Configura el TextWatcher para la contraseña
        etPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                clearPasswordError()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun clearUsernameError() {
        ilUsername.error = null
    }

    private fun clearPasswordError() {
        ilPassword.error = null
    }

    // Método solo para pruebas
    fun setViewModelOnlyTest(viewModel: AuthViewModel) {
        this.viewModel = viewModel
    }
}