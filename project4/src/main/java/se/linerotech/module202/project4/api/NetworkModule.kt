package se.linerotech.module202.project4.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import se.linerotech.module202.project4.BuildConfig

object NetworkModule {

    private const val BASE_URL = "https://api.edamam.com/"
    private val authQueryInterceptor = Interceptor { chain ->
        val original = chain.request()
        val newUrl = original.url.newBuilder()
            .addQueryParameter("app_id", BuildConfig.EDAMAM_APP_ID)
            .addQueryParameter("app_key", BuildConfig.EDAMAM_APP_KEY)
            .build()
        val newReq = original.newBuilder().url(newUrl).build()
        chain.proceed(newReq)
    }

    private val userHeaderInterceptor = Interceptor { chain ->
        val req = chain.request().newBuilder()
            .addHeader("Edamam-Account-User", BuildConfig.EDAMAM_USER_ID)
            .build()
        chain.proceed(req)
    }

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(authQueryInterceptor)
            .addInterceptor(userHeaderInterceptor)
            .build()
    }

    val api: RecipeService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RecipeService::class.java)
    }
}
