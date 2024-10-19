package com.uniandes.project.abcall.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.widget.Button
// import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.uniandes.project.abcall.R
import com.uniandes.project.abcall.config.TokenManager

import com.uniandes.project.abcall.databinding.ActivityUserRegisterBinding

import com.uniandes.project.abcall.repositories.rest.RegisterUserClient


// import com.uniandes.project.abcall.ui.dialogs.CustomDialogFragment

import com.uniandes.project.abcall.viewmodels.RegisterUserViewModel


class UserRegisterActivity : CrossIntentActivity() {

    private lateinit var etFullName: TextInputEditText
    private lateinit var etUserName: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etCheckPassword: TextInputEditText


    private lateinit var ilFullName: TextInputLayout
    private lateinit var ilUsername: TextInputLayout
    private lateinit var ilPasword: TextInputLayout
    private lateinit var ilCheckpassword: TextInputLayout

    private lateinit var btnRegister: Button
    private lateinit var btnClear: Button

    private lateinit var binding: ActivityUserRegisterBinding
    private lateinit var viewModel: RegisterUserViewModel

    private val registerClient = RegisterUserClient()
    private lateinit var tokenManager: TokenManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityUserRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        tokenManager = TokenManager(binding.root.context)

        viewModel = RegisterUserViewModel(registerClient)


        etFullName = findViewById(R.id.et_fullName)
        etUserName = findViewById(R.id.et_username)

        etPassword = findViewById(R.id.et_password)

        etCheckPassword = findViewById(R.id.et_checkpassword)

        ilFullName = findViewById(R.id.ilFullName)
        ilUsername = findViewById(R.id.ilUsername)
        ilPasword = findViewById(R.id.ilPasword)
        ilCheckpassword = findViewById(R.id.ilCheckpassword)


        btnRegister = findViewById(R.id.btn_register)


        btnRegister.setOnClickListener {
            validateForm()
            nextActivity(LoginActivity::class.java)
        }

        btnClear = findViewById(R.id.btn_clear)

        btnClear.setOnClickListener({
            etFullName.text?.clear()
            etUserName.text?.clear()
            etPassword.text?.clear()
            etCheckPassword.text?.clear()

        })

        setupTextWatchers()
    }

    private fun validateForm() {

        ilFullName.error = null
        ilUsername.error = null
        ilPasword.error = null
        ilCheckpassword.error = null

        val fullName = etFullName.text.toString().trim()
        val username = etUserName.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val checkPassword = etCheckPassword.text.toString().trim()

        var isValid = true

        if (TextUtils.isEmpty(username)) {
            ilFullName.error = "FullName"
            isValid = false
        }

        if (TextUtils.isEmpty(username)) {
            ilUsername.error = "User"
            isValid = false
        }

        if (TextUtils.isEmpty(password)) {
            ilPasword.error = "Password"
            isValid = false
        }

        if (TextUtils.isEmpty(checkPassword)) {
            ilCheckpassword.error = "CheckPassword"
            isValid = false
        }

        if (checkPassword != password){
            ilPasword.error = "password"
            ilCheckpassword.error = "CheckPassword"
            isValid = false
        }

        if (isValid) {
            viewModel.registerUser(fullName, username, password, checkPassword)
        }
    }

    private fun setupTextWatchers() {
        // Configura el TextWatcher para el nombre de usuario
        etFullName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                clearFullNameError()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        etUserName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                clearUserNameError()
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

        etCheckPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                clearCheckPasswordError()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun clearFullNameError() {
        ilFullName.error = null
    }

    private fun clearUserNameError() {
        ilUsername.error = null
    }

    private fun clearPasswordError() {
        ilPasword.error = null
    }

    private fun clearCheckPasswordError() {
        ilPasword.error = null
    }
}