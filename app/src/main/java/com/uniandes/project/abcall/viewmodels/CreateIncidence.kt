package com.uniandes.project.abcall.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.uniandes.project.abcall.config.TokenManager
import com.uniandes.project.abcall.repositories.rest.AuthClient

class CreateIncidence (
    private val type: Int,
    private val channel: String,
    private val subject: String,
    private val detail: String,
    private val files: List<Any>

    ) : ViewModel() {


    fun createIncidence(type: Int, channel: String, subject: String, detail: String, files: List<Any>) {

    }
}