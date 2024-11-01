package com.uniandes.project.abcall.models

import android.net.Uri

class Incidence (
    val personId: Int,
    val subject: String,
    val detail: String,
    val type: String,
    val files: List<Uri>
)