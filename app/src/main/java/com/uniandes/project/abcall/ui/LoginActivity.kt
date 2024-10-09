package com.uniandes.project.abcall.ui

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.uniandes.project.abcall.R
import com.uniandes.project.abcall.config.TokenManager
import com.uniandes.project.abcall.databinding.ActivityLoginBinding
import com.uniandes.project.abcall.repositories.rest.AuthClient
import com.uniandes.project.abcall.viewmodels.AuthViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: AuthViewModel
    private val authClient = AuthClient()
    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        tokenManager = TokenManager(binding.root.context)

        viewModel = AuthViewModel(authClient, tokenManager)

        val usernameEditText: EditText = findViewById(R.id.et_username)
        val passwordEditText: EditText = findViewById(R.id.et_password)
        val loginButton: Button = findViewById(R.id.btn_log_in)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            viewModel.authenticate(username, password)
        }

        viewModel.token.observe(this, { token ->

            if (token != null) {
                Log.d("LoginActivity", "Token: ${tokenManager.getAuth()?.token}")
                // Maneja el token (navegación, almacenamiento, etc.)
            } else {
                // Maneja errores de autenticación
                Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
            }
        })
    }
}