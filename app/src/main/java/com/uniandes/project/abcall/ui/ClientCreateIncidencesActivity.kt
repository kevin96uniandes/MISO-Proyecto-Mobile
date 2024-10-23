package com.uniandes.project.abcall.ui

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.uniandes.project.abcall.R
import com.uniandes.project.abcall.config.TokenManager
import com.uniandes.project.abcall.databinding.ActivityClienteCreateIncidencesBinding
import com.uniandes.project.abcall.repositories.rest.AuthClient
import java.io.File


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

        var type = 0


        btnTypes.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this)

            alertDialog.setIcon(R.drawable.ic_dashboard_black_24dp)

            alertDialog.setTitle("Tipo:")

            val listItems = arrayOf("Petición", "Queja/Reclamo", "Sugerencia")


            alertDialog.setSingleChoiceItems(listItems, checkedItem[0]) { dialog, which ->
                checkedItem[0] = which
                type = which
                btnTypes.text = listItems[type]
                dialog.dismiss()
            }

            val customAlertDialog = alertDialog.create()
            customAlertDialog.show()

        }


        btnLoadFiles = findViewById(R.id.btn_load_files)


        btnLoadFiles.setOnClickListener {
            openDirectory()
        }


        btnCancel = findViewById(R.id.btn_cancel)

        btnCancel.setOnClickListener({
            etSubject.text?.clear()
            etDetail.text?.clear()
        })

        btnSend = findViewById(R.id.btn_send)

        btnSend.setOnClickListener{
            val a = validateForm()

            if (a){
                Toast.makeText(this, "Registro Exitoso", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "Registro Failed!", Toast.LENGTH_SHORT).show()
            }
        }

        /*

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
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.type = "*/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        try {
            startActivityForResult(Intent.createChooser(intent, "Select a file"), 100)
        } catch (exception: Exception){
            Toast.makeText(this, "Please install a file manager", Toast.LENGTH_SHORT).show()
        }
    }


    private fun validateForm(): Boolean {

        ilSubject.error = null
        ilDetail.error = null

        val subject = etSubject.text.toString().trim()
        val detail = etDetail.text.toString().trim()

        var isValid = true

        if (TextUtils.isEmpty(subject)) {
            ilSubject.error = "Por favor ingresa un asunto"
            isValid = false
        }

        if (TextUtils.isEmpty(detail)) {
            ilDetail.error = "Por favor ingresa un detalle de la incidencia"
            isValid = false
        }

        return isValid

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
