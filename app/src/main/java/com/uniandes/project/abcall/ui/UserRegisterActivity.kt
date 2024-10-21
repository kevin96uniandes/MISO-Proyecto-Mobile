package com.uniandes.project.abcall.ui

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
    private var selectedIdentityTypeLabel: String = ""
    private lateinit var twErrorMessage: TextView
    private lateinit var etFirstName: TextInputEditText
    private lateinit var etLastName: TextInputEditText
    private lateinit var spIdentificationType: Spinner
    private lateinit var etIdentificationNumber: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPhone: TextInputEditText
    private lateinit var etUserName: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etCheckPassword: TextInputEditText


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

        twErrorMessage = findViewById(R.id.error_message_1)
        etFirstName = findViewById(R.id.et_firstName)
        etLastName = findViewById(R.id.et_lastName)
        spIdentificationType = findViewById(R.id.sp_dentification_type)
        etIdentificationNumber = findViewById(R.id.et_identification_number)
        etEmail = findViewById(R.id.et_email)
        etPhone = findViewById(R.id.et_phone)
        etUserName = findViewById(R.id.et_username)

        etPassword = findViewById(R.id.et_password)

        etCheckPassword = findViewById(R.id.et_checkPassword)

        ilFirstName = findViewById(R.id.ilFirstName)
        ilLastName = findViewById(R.id.ilLastName)
        ilIdentificationType = findViewById(R.id.ilSpIdentificationType)
        ilIdentificationNumber = findViewById(R.id.ilIdentificationNumber)
        ilPhone = findViewById(R.id.ilPhone)
        ilEmail = findViewById(R.id.ilEmail)
        ilUsername = findViewById(R.id.ilUsername)
        ilPassword = findViewById(R.id.ilPassword)
        ilCheckPassword = findViewById(R.id.ilCheckPassword)


        btnRegister = findViewById(R.id.btn_register)

        var identificationTypes = listOf("-- Seleccione género --") + IdentificationType.entries.map { it.type }

        val adapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_item, identificationTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        fillSpinner(spIdentificationType, identificationTypes)
        spinnerEvents(spIdentificationType, identificationTypes)
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

        viewModel.code.observe(this) { code ->
            if (code != null){
                val dialog = CustomDialogFragment().newInstance(
                    "Registro de usuario",
                    "Usuario creado exitosamente",
                    R.raw.success
                ) {
                    Toast.makeText(this, "Usuario creado exitosamente", Toast.LENGTH_SHORT).show()
                    nextActivity(LoginActivity::class.java)
                }
                dialog.show(supportFragmentManager, "CustomDialog")
            } else {
                val dialog = CustomDialogFragment().newInstance(
                    "Registro de usuario",
                    "Error creando usuario",
                    R.raw.error
                )
                dialog.show(supportFragmentManager, "CustomDialog")
                Toast.makeText(this, "Error creando usuario", Toast.LENGTH_SHORT).show()
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

        spIdentificationType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // Obtener el tipo de identificación seleccionado
                val selectedType = IdentificationType.entries[position]

                // Obtener el ID del enum seleccionado
                idIdentityType = selectedType.getIdType()

                // Aquí puedes hacer lo que necesites con el ID seleccionado
                Toast.makeText(this@UserRegisterActivity, "ID seleccionado: $idIdentityType", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No se seleccionó ningún elemento
            }
        }

        setupTextWatchers()
    }

    private fun isValidForm(): Boolean {
        // Limpiar errores previos
        ilFirstName.error = null
        ilLastName.error = null
        ilUsername.error = null
        ilPassword.error = null
        ilCheckPassword.error = null
        ilEmail.error = null // Añade el TextInputLayout para el correo electrónico
        ilPhone.error = null // Añade el TextInputLayout para el teléfono
        ilIdentificationNumber.error = null // Añade el TextInputLayout para el número de identificación


        // Obtener los valores de los EditText
        val firstName = etFirstName.text.toString().trim()
        val lastName = etLastName.text.toString().trim()
        val username = etUserName.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val checkPassword = etCheckPassword.text.toString().trim()
        val email = etEmail.text.toString().trim() // Suponiendo que tienes un EditText para el correo
        val phoneNumber = etPhone.text.toString().trim() // Suponiendo que tienes un EditText para el teléfono
        val idNumber = etIdentificationNumber.text.toString().trim() // Suponiendo que tienes un EditText para el número de identificación

        var isValid = true

        // Validar campos vacíos
        if (TextUtils.isEmpty(firstName)) {
            ilFirstName.error = "Nombres son obligatorios"
            isValid = false
        }

        if (TextUtils.isEmpty(lastName)) {
            ilLastName.error = "Apellidos son obligatorios"
            isValid = false
        }

        if (TextUtils.isEmpty(username)) {
            ilUsername.error = "Usuario es obligatorio"
            isValid = false
        }

        if (TextUtils.isEmpty(password)) {
            ilPassword.error = "Contraseña es obligatoria"
            isValid = false
        }

        if (TextUtils.isEmpty(checkPassword)) {
            ilCheckPassword.error = "Confirmación de contraseña es obligatoria"
            isValid = false
        }

        if (TextUtils.isEmpty(email)) {
            ilEmail.error = "El correo electrónico es obligatorio"
            isValid = false
        } else if (!isEmailValid(email)) {
            ilEmail.error = "Correo electrónico no es válido"
            isValid = false
        }

        if (TextUtils.isEmpty(phoneNumber)) {
            ilPhone.error = "El número de teléfono es obligatorio"
            isValid = false
        } else if (!isPhoneNumberValid(phoneNumber)) {
            ilPhone.error = "Número de teléfono no es válido"
            isValid = false
        }

        if (idIdentityType < 2 ){
            twErrorMessage.visibility = View.VISIBLE
        }

        if (TextUtils.isEmpty(idNumber)) {
            ilIdentificationNumber.error = "El número de identificación es obligatorio"
            isValid = false
        } else if (!isIdNumberValid(idNumber)) {
            ilIdentificationNumber.error = "Número de identificación no es válido"
            isValid = false
        }

        // Validar coincidencia de contraseñas
        if (checkPassword != password) {
            ilPassword.error = "Contraseñas no coinciden"
            ilCheckPassword.error = "Contraseñas no coinciden"
            isValid = false
        }

        // Validar complejidad de la contraseña
        if (!isPasswordValid(password)) {
            ilPassword.error = "Contraseñas deben tener al menos 8 caracteres, incluyendo una mayúscula, una minúscula, y un número."
            isValid = false
        }

        return isValid
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

    fun fillSpinner(spinner: Spinner, items: List<String>){
        val adapter = CustomSpinnerAdapter(binding.root.context, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun spinnerEvents (spinner: Spinner, data: List<String>) {
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = data[position]
                when(spinner.id) {
                    R.id.sp_dentification_type -> {
                        if(position == 1 && selectedIdentityTypeLabel.isNotEmpty()) {
                            twErrorMessage.text = "Campo requerido y debe seleccionar un elemento"
                            twErrorMessage.visibility = View.VISIBLE
                        }else{
                            twErrorMessage.visibility = View.GONE
                            selectedIdentityTypeLabel = selectedItem
                        }
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
    }

}