package com.uniandes.project.abcall.repositories.rest

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.uniandes.project.abcall.config.ApiResult
import com.uniandes.project.abcall.config.RetrofitClient
import com.uniandes.project.abcall.enums.Technology
import com.uniandes.project.abcall.repositories.rest.RegisterUserClient.RegisterResponse
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream

class IncidenceClient {

    fun createIncidence(body: CreateIncidenceRequest, contentResolver: ContentResolver, callback: (ApiResult<Unit>) -> Unit) {
        val multipartBody = body.toCreateIncidenceRequest(contentResolver)
        RetrofitClient.apiService.createIncidence(
            multipartBody.personId,
            multipartBody.type,
            multipartBody.channel,
            multipartBody.subject,
            multipartBody.detail,
            multipartBody.files
        ).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    Log.d("Response incidence", response.toString())
                } else {
                    val errorMessage = response.errorBody()?.string()
                    Log.d("Response error", errorMessage!!)
                    callback(ApiResult.Error(response.code(), errorMessage))
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Log.e("MainActivity", "Failure: ${t.printStackTrace()}")
                callback(ApiResult.NetworkError)
            }
        })
    }


    data class CreateIncidenceRequest(
        val personId: Int,
        val type: String,
        val subject: String,
        val detail: String,
        val files: List<Uri>
    )

    data class CreateIncidenceRequestBody(
        val personId: RequestBody,
        val type: RequestBody,
        val channel: RequestBody,
        val subject: RequestBody,
        val detail: RequestBody,
        val files: MutableList<MultipartBody.Part?> = mutableListOf()
    )

    internal fun CreateIncidenceRequest.toCreateIncidenceRequest(contentResolver: ContentResolver): CreateIncidenceRequestBody {
        val filesMultipart = files.map { uriToMultipart(it, contentResolver) }.toMutableList()

        return CreateIncidenceRequestBody(
            personId = RequestBody.create(MediaType.parse("text/plain"), personId.toString()),
            type = RequestBody.create(MediaType.parse("text/plain"), type),
            channel = RequestBody.create(MediaType.parse("text/plain"), Technology.MOBILE.channel),
            subject = RequestBody.create(MediaType.parse("text/plain"), subject),
            detail = RequestBody.create(MediaType.parse("text/plain"), detail),
            files = filesMultipart
        )
    }

    fun uriToMultipart(uri: Uri, contentResolver: ContentResolver): MultipartBody.Part? {
        val inputStream = getInputStreamFromUri(uri, contentResolver)
        if (inputStream != null) {
            val fileName = uri.lastPathSegment ?: "file" // o algún nombre por defecto
            val requestFile = RequestBody.create(MediaType.parse(contentResolver.getType(uri) ?: "application/octet-stream"), inputStream.readBytes())
            return MultipartBody.Part.createFormData("files", fileName, requestFile)
        }
        return null
    }

    private fun getInputStreamFromUri(uri: Uri, contentResolver: ContentResolver): InputStream? {
        return try {
            contentResolver.openInputStream(uri)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            null
        }
    }

    private fun getRealPathFromURI(uri: Uri, contentResolver: ContentResolver): String? {
        var cursor: Cursor? = null
        val projection = arrayOf(MediaStore.Images.Media.DATA)

        return try {
            cursor = contentResolver.query(uri, projection, null, null, null)
            val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            if (cursor != null && cursor.moveToFirst()) {
                cursor.getString(columnIndex!!)
            } else {
                uri.path // Retorna la ruta si no se puede encontrar la ruta real
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null // Retorna null si hay algún error
        } finally {
            cursor?.close()
        }
    }

}