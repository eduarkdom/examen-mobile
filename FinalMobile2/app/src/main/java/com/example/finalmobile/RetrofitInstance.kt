package com.example.finalmobile

import retrofit2.Retrofit

object RetrofitInstance {
    private const val BASE_URL = "https://mindicador.cl/api" // Reemplaza con la URL base de tu API

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .build()
    }
}
