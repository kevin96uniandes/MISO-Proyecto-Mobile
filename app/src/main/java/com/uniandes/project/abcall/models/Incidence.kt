package com.uniandes.project.abcall.models

import java.io.File

class Incidence (
    val personId: Int,
    val subject: String,
    val detail: String,
    val type: String,
    val files: List<File>,
    val idCompany: Int
)