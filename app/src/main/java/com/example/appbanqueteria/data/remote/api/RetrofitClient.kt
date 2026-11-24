package com.example.appbanqueteria.data.remote.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // Puerto 9000 = API Gateway (requiere corregir Path=/api/reservaciones/** en docker-compose.yml)
    // Si quieres probar directo sin pasar por el Gateway, cambia 9000 por 8082
    private const val BASE_URL = "http://localhost:9000/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    val reservasApi: ReservasApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ReservasApi::class.java)
    }
}