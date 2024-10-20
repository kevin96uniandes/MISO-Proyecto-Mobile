package com.uniandes.project.abcall.ui

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.uniandes.project.abcall.R
import com.uniandes.project.abcall.config.TokenManager
import com.uniandes.project.abcall.databinding.ActivityClienteCreateIncidencesBinding
import com.uniandes.project.abcall.repositories.rest.AuthClient

class ClientCreateIncidencesActivity : CrossIntentActivity() {

    private lateinit var etSubject: TextInputEditText
    private lateinit var etDetail: TextInputEditText

    private lateinit var ilSubject: TextInputLayout
    private lateinit var ilDetail: TextInputLayout

    private lateinit var btnTypes: Button
    private lateinit var btnLoadFiles: Button
    private lateinit var btnCancel: Button
    private lateinit var btnSend: Button

    private lateinit var binding: ActivityClienteCreateIncidencesBinding
    // private lateinit var viewModel: AuthViewModel
    private val authClient = AuthClient()
    private lateinit var tokenManager: TokenManager




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityClienteCreateIncidencesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        tokenManager = TokenManager(binding.root.context)

        etSubject = findViewById(R.id.et_subject)
        etDetail = findViewById(R.id.et_detail)

        ilSubject = findViewById(R.id.il_subject)
        ilDetail = findViewById(R.id.il_detail)

        btnTypes = findViewById(R.id.btn_types)

        val checkedItem = intArrayOf(-1)

        btnTypes.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this)

            alertDialog.setIcon(R.drawable.logo)

            alertDialog.setTitle("Choose an Item")

            val listItems = arrayOf("Android Development", "Web Development", "Machine Learning")

            alertDialog.setSingleChoiceItems(listItems, checkedItem[0]) { dialog, which ->
                checkedItem[0] = which

                dialog.dismiss()
            }
        }


        btnLoadFiles = findViewById(R.id.btn_load_files)

        btnLoadFiles.setOnClickListener {
            val intent = openDirectory()
        }



        btnCancel = findViewById(R.id.btn_cancel)
        btnSend = findViewById(R.id.btn_send)



        /*
        btnRegister.setOnClickListener {
            val intent = Intent(this, UserRegisterActivity::class.java)
            startActivity(intent)
        }



        viewModel.token.observe(this) { token ->
            if (token != null) {
                nextActivity(ClientHomeActivity::class.java)
            } else {
                val dialog = CustomDialogFragment().newInstance(
                    "Inicio de sesión",
                    "Usuario y/o contraseña incorrecta",
                    R.raw.error
                )
                dialog.show(supportFragmentManager, "CustomDialog")
                Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
            }
        }

        setupTextWatchers()
        */

    }


    private fun openDirectory() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, ".")
        }
    }





    private fun validateForm() {

        ilSubject.error = null
        ilDetail.error = null

        val username = etSubject.text.toString().trim()
        val password = etDetail.text.toString().trim()

        var isValid = true

        if (TextUtils.isEmpty(username)) {
            ilSubject.error = "Por favor ingresa tu nombre de usuario"
            isValid = false
        }

        if (TextUtils.isEmpty(password)) {
            ilDetail.error = "Por favor ingresa tu contraseña"
            isValid = false
        }

        /*
        if (isValid) {
            viewModel.authenticate(username, password)
        }
        */
    }

    private fun setupTextWatchers() {
        etSubject.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                clearSubjectError()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        etDetail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                clearDetailError()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun clearSubjectError() {
        ilSubject.error = null
    }

    private fun clearDetailError() {
        ilDetail.error = null
    }
}