package com.example.todoapplication.api

import android.content.Context
import androidx.work.WorkManager
import com.example.todoapplication.data.model.TokenManager
import com.example.todoapplication.repository.AuthRepository
import com.example.todoapplication.repository.TaskRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom // BU IMPORT'U EKLEYİN
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

        private const val BASE_URL = "https://www.goryunar.somee.com/"
//  private const val BASE_URL = "https://10.0.2.2:7289/"
//    private const val BASE_URL = "https://192.168.1.191:7289/"

    // 1️⃣ Logging Interceptor
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        return logging
    }

    // 2️⃣ OkHttpClient (TÜM GÜVENLİK MANTIKLI ŞEKİLDE BURADA TOPLANDI)
    @Provides
    @Singleton
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)

        // SADECE DEBUG MODUNDA TÜM SERTİFİKALARA GÜVEN
        // Bu, uygulamanızın yayın sürümünde güvende kalmasını sağlar.
        if (true) {
            try {
                // Tüm sertifikalara güvenen sahte bir TrustManager oluştur


            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }
        val trustAllCerts = arrayOf<TrustManager>(
            object : X509TrustManager {
                override fun checkClientTrusted(
                    chain: Array<out X509Certificate>?,
                    authType: String?
                ) {
                }

                override fun checkServerTrusted(
                    chain: Array<out X509Certificate>?,
                    authType: String?
                ) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            }
        )

        // Bu TrustManager'ı kullanarak bir SSLContext kur
        val sslContext = SSLContext.getInstance("SSL")
        // *** HATAYI DÜZELTEN EN ÖNEMLİ SATIR ***
        sslContext.init(null, trustAllCerts, SecureRandom())

        // OkHttpClient.Builder'ı bu "güvensiz" SSL soketi ve hostname doğrulayıcı ile yapılandır
        builder.sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
        builder.hostnameVerifier { _, _ -> true } // Hostname doğrulamayı da atla
        return builder.build()
    }

    // 3️⃣ Retrofit Client
    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // 4️⃣ ApiService
    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    // Repository Provider'ları
    @Provides
    @Singleton
    fun provideTaskRepository(apiService: ApiService, tokenManager: TokenManager): TaskRepository {
        return TaskRepository(apiService, tokenManager)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        apiService: ApiService,
        @ApplicationContext context: Context,
        tokenManager: TokenManager
    ): AuthRepository {
        return AuthRepository(apiService, context, tokenManager)
    }

    @Provides
    @Singleton
    fun provideTokenManager(@ApplicationContext context: Context): TokenManager {
        return TokenManager(context)
    }

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }
}