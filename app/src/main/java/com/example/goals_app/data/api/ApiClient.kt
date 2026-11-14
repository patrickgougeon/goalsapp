package com.example.goals_app.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// "object" em Kotlin = Singleton (uma única instância no app inteiro).
// É o único lugar que conhece a URL do servidor.
object ApiClient {

    // Emulador: 10.0.2.2 é o "localhost" do seu PC.
    // Celular físico: troque pelo IP do PC na rede Wi-Fi, ex.: "http://192.168.0.10:8080/"
    const val BASE_URL = "http://10.0.2.2:8080/"

    // Swagger (será usado na tela de perfil via Intent implícita)
    const val SWAGGER_URL = BASE_URL + "swagger-ui.html"

    // O log mostra no Logcat cada requisição/resposta HTTP (ótimo para depurar).
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    // "by lazy" = só cria na primeira vez que for usado (e reaproveita depois).
    val api: MetasApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())  // JSON <-> objetos Kotlin
            .build()
            .create(MetasApi::class.java)                         // Retrofit implementa a interface por você
    }
}
