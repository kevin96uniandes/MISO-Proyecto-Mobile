package com.uniandes.project.abcall.ui

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
// import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.uniandes.project.abcall.IdentificationType
import com.uniandes.project.abcall.R
import com.uniandes.project.abcall.config.ApiResult
import com.uniandes.project.abcall.config.TokenManager

import com.uniandes.project.abcall.databinding.ActivityUserRegisterBinding

import com.uniandes.project.abcall.repositories.rest.RegisterUserClient
import com.uniandes.project.abcall.ui.components.CustomSpinnerAdapter
import com.uniandes.project.abcall.ui.dialogs.CustomDialogFragment
import com.uniandes.project.abcall.viewmodels.RegisterUser


// import com.uniandes.project.abcall.ui.dialogs.CustomDialogFragment

import com.uniandes.project.abcall.viewmodels.RegisterUserViewModel


class UserRegisterActivity : CrossIntentActivity() {

    private var idIdentityType = -1;
    private lateinit var etFirstName: TextInputEditText
    private lateinit var etLastName: TextInputEditText
    private lateinit var etIdentificationNumber: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPhone: TextInputEditText
    private lateinit var etUserName: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etCheckPassword: TextInputEditText
    private lateinit var etIdentificationType: TextInputEditText


    private lateinit var ilFirstName: TextInputLayout
    private lateinit var ilLastName: TextInputLayout
    private lateinit var ilIdentificationType: TextInputLayout
    private lateinit var ilIdentificationNumber: TextInputLayout
    private lateinit var ilEmail: TextInputLayout
    private lateinit var ilPhone: TextInputLayout
    private lateinit var ilUsername: TextInputLayout
    private lateinit var ilPassword: TextInputLayout
    private lateinit var ilCheckPassword: TextInputLayout

    private lateinit var btnRegister: Button
    private lateinit var btnClear: Button

    private lateinit var binding: ActivityUserRegisterBinding
    private lateinit var viewModel: RegisterUserViewModel

    private val registerClient = RegisterUserClient()
    private lateinit var tokenManager: TokenManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUserRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        tokenManager = TokenManager(binding.root.context)

        viewModel = RegisterUserViewModel(registerClient)

        etFirstName = findViewById(R.id.et_firstName)
        etLastName = findViewById(R.id.et_lastName)
        etIdentificationNumber = findViewById(R.id.et_identification_number)
        etEmail = findViewById(R.id.et_email)
        etPhone = findViewById(R.id.et_phone)
        etUserName = findViewById(R.id.et_username)
        etIdentificationType = findViewById(R.id.et_identification_type)

        etPassword = findViewById(R.id.et_password)

        etCheckPassword = findViewById(R.id.et_checkPassword)

        ilFirstName = findViewById(R.id.ilFirstName)
        ilLastName = findViewById(R.id.ilLastName)
        ilIdentificationNumber = findViewById(R.id.ilIdentificationNumber)
        ilPhone = findViewById(R.id.ilPhone)
        ilEmail = findViewById(R.id.ilEmail)
        ilUsername = findViewById(R.id.ilUsername)
        ilPassword = findViewById(R.id.ilPassword)
        ilCheckPassword = findViewById(R.id.ilCheckPassword)
        ilIdentificationType = findViewById(R.id.ilIdentificationType)

        btnRegister = findViewById(R.id.btn_register)

        var identificationTypes = listOf("-- Seleccione género --") + IdentificationType.entries.map { it.type }

        val adapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_item, identificationTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        btnRegister.setOnClickListener {
            if ( isValidForm() ) {
                val userRegister = RegisterUser(
                    names = etFirstName.text.toString(),
                    lastName = etLastName.text.toString(),
                    username = etUserName.text.toString(),
                    password = etPassword.text.toString(),
                    confirmPassword = etCheckPassword.text.toString(),
                    phone = etPhone.text.toString(),
                    email = etEmail.text.toString(),
                    identificationNumber = etIdentificationNumber.text.toString().toLong(),
                    idIdentityType = idIdentityType
                )
                viewModel.registerUser(userRegister)

            }
        }

        viewModel.result.observe(this) { result ->
            when (result) {
                is ApiResult.Success -> {
                    val dialog = CustomDialogFragment().newInstance(
                        "Registro de usuario",
                        "Usuario creado exitosamente",
                        R.raw.success
                    ) {
                        Toast.makeText(this, "Usuario creado exitosamente", Toast.LENGTH_SHORT).show()
                        nextActivity(LoginActivity::class.java)
                    }
                    dialog.show(supportFragmentManager, "CustomDialog")
                }
                is ApiResult.Error -> {
                    when (result.code) {
                        409 -> {
                            val dialog = CustomDialogFragment().newInstance(
                                "Registro de usuario",
                                "El usuario ya existe. Por favor, elige otro nombre de usuario.",
                                R.raw.error
                            )
                            dialog.show(supportFragmentManager, "CustomDialog")
                            Toast.makeText(this, "Error: Usuario ya registrado", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            val dialog = CustomDialogFragment().newInstance(
                                "Registro de usuario",
                                "Error al registrar el usuario: ${result.message}",
                                R.raw.error
                            )
                            dialog.show(supportFragmentManager, "CustomDialog")
                            Toast.makeText(this, "Error creando usuario", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                ApiResult.NetworkError -> {
                    val dialog = CustomDialogFragment().newInstance(
                        "Registro de usuario",
                        "No se pudo conectar con el servidor. Por favor, verifica tu conexión a internet.",
                        R.raw.no_network
                    )
                    dialog.show(supportFragmentManager, "CustomDialog")
                    Toast.makeText(this, "Error de red", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnClear = findViewById(R.id.btn_clear)

        btnClear.setOnClickListener({
            etFirstName.text?.clear()
            etLastName.text?.clear()
            etUserName.text?.clear()
            etPassword.text?.clear()
            etCheckPassword.text?.clear()

        })

        etIdentificationType.setOnClickListener {
            val items = IdentificationType.entries.map { "${it.id} - ${it.type}" }.toTypedArray()
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Selecciona una opción")
                .setSingleChoiceItems(items, -1) { dialog, which ->
                    val selectedItem = items[which]
                    etIdentificationType.setText(selectedItem)
                    idIdentityType = selectedItem.split("-")[0].trim().toInt()
                    clearIdentificationTypeError()
                    dialog.dismiss()
                }
                .setNegativeButton("Cancelar") { dialog, which ->
                    dialog.dismiss()
                }
            builder.show()
        }

        setupTextWatchers()
    }

    private fun isValidForm(): Boolean {
        var isValid = true
        // Limpiar errores previos
        ilFirstName.error = null
        ilLastName.error = null
        ilUsername.error = null
        ilPassword.error = null
        ilCheckPassword.error = null
        ilEmail.error = null
        ilPhone.error = null
        ilIdentificationNumber.error = null

        // Obtener los valores de los EditText
        val firstName = etFirstName.text.toString().trim()
        val lastName = etLastName.text.toString().trim()
        val username = etUserName.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val checkPassword = etCheckPassword.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val phoneNumber = etPhone.text.toString().trim()
        val idNumber = etIdentificationNumber.text.toString().trim()

        // Validar campo por campo y detenerse en el primer error
        if (TextUtils.isEmpty(firstName)) {
            ilFirstName.error = "Nombres son obligatorios"
            scrollToView(ilFirstName) // Desplazar el formulario hacia el campo con error
            isValid = false
        }

        if (TextUtils.isEmpty(lastName)) {
            ilLastName.error = "Apellidos son obligatorios"
            scrollToView(ilLastName)
            isValid = false
        }

        if (TextUtils.isEmpty(username)) {
            ilUsername.error = "Usuario es obligatorio"
            scrollToView(ilUsername)
            isValid = false
        }

        if (TextUtils.isEmpty(password)) {
            ilPassword.error = "Contraseña es obligatoria"
            scrollToView(ilPassword)
            isValid = false
        }

        if (TextUtils.isEmpty(checkPassword)) {
            ilCheckPassword.error = "Confirmación de contraseña es obligatoria"
            scrollToView(ilCheckPassword)
            isValid = false
        }

        if (checkPassword != password) {
            ilPassword.error = "Contraseñas no coinciden"
            ilCheckPassword.error = "Contraseñas no coinciden"
            scrollToView(ilPassword)
            isValid = false
        }

        if (!isPasswordValid(password)) {
            ilPassword.error = "Contraseñas deben tener al menos 8 caracteres, incluyendo una mayúscula, una minúscula, y un número."
            scrollToView(ilPassword)
            isValid = false
        }

        if (TextUtils.isEmpty(email)) {
            ilEmail.error = "El correo electrónico es obligatorio"
            scrollToView(ilEmail)
            isValid = false
        } else if (!isEmailValid(email)) {
            ilEmail.error = "Correo electrónico no es válido"
            scrollToView(ilEmail)
            isValid = false
        }

        if (TextUtils.isEmpty(phoneNumber)) {
            ilPhone.error = "El número de teléfono es obligatorio"
            scrollToView(ilPhone)
            isValid = false
        } else if (!isPhoneNumberValid(phoneNumber)) {
            ilPhone.error = "Número de teléfono no es válido"
            scrollToView(ilPhone)
            isValid = false
        }

        if (TextUtils.isEmpty(idNumber)) {
            ilIdentificationNumber.error = "El número de identificación es obligatorio"
            scrollToView(ilIdentificationNumber)
            isValid = false
        } else if (!isIdNumberValid(idNumber)) {
            ilIdentificationNumber.error = "Número de identificación no es válido"
            scrollToView(ilIdentificationNumber)
            isValid = false
        }

        if (idIdentityType == -1) {
            ilIdentificationType.error = "Debe seleccionar un tipo de documento"
            isValid = false
        }

        // Si todos los campos son válidos
        return isValid
    }

    private fun scrollToView(view: View) {
        view.parent.requestChildFocus(view, view)
    }

    // Funciones de validación
    private fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isPhoneNumberValid(phoneNumber: String): Boolean {
        return phoneNumber.length == 10 && phoneNumber.all { it.isDigit() }
    }

    private fun isIdNumberValid(idNumber: String): Boolean {
        return idNumber.length == 10 && idNumber.all { it.isDigit() }
    }

    private fun isPasswordValid(password: String): Boolean {
        val minLength = 8
        val hasUpperCase = password.any { it.isUpperCase() }
        val hasLowerCase = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }

        return password.length >= minLength && hasUpperCase && hasLowerCase && hasDigit
    }

    private fun setupTextWatchers() {
        etFirstName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                clearFirstNameError()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        etLastName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                clearLastNameError()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        etIdentificationNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                clearIdentificationNumberError()
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}

        })

        etPhone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                clearPhoneError()
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}

        })

        etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                clearEmailError()
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}

        })

        etUserName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                clearUserNameError()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

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

    private fun clearFirstNameError() {
        ilFirstName.error = null
    }

    private fun clearLastNameError() {
        ilLastName.error = null
    }

    private fun clearIdentificationNumberError() {
        ilIdentificationNumber.error = null
    }

    private fun clearEmailError() {
        ilEmail.error = null
    }

    private fun clearPhoneError() {
        ilPhone.error = null
    }

    private fun clearUserNameError() {
        ilUsername.error = null
    }

    private fun clearPasswordError() {
        ilPassword.error = null
    }

    private fun clearCheckPasswordError() {
        ilCheckPassword.error = null
    }

    private fun clearIdentificationTypeError() {
        ilIdentificationType.error = null
    }
}