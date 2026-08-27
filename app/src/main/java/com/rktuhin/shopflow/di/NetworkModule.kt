package com.rktuhin.shopflow.di

import com.rktuhin.shopflow.BuildConfig
import com.rktuhin.shopflow.data.remote.auth.AuthInterceptor
import com.rktuhin.shopflow.data.remote.auth.NoOpAuthTokenProvider
import com.rktuhin.shopflow.domain.auth.AuthTokenProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // 30 seconds is a reasonable default for mobile clients to handle variable network conditions.
    private const val TIMEOUT_SECONDS = 30L

    @Provides
    @Singleton
    fun provideAuthTokenProvider(noOpAuthTokenProvider: NoOpAuthTokenProvider): AuthTokenProvider {
        return noOpAuthTokenProvider
    }

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor()
        // Ensure BODY logging only in DEBUG to avoid exposing sensitive data in RELEASE
        interceptor.level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }

        // Explicitly redact sensitive headers to prevent credentials/PII leaking into logs
        interceptor.redactHeader("Authorization")
        interceptor.redactHeader("Cookie")
        interceptor.redactHeader("Set-Cookie")
        interceptor.redactHeader("X-API-Key")
        interceptor.redactHeader("API-Key")
        interceptor.redactHeader("Proxy-Authorization")

        return interceptor
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            // AuthInterceptor must precede LoggingInterceptor so the logger sees and can redact the headers.
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            // Call timeout is not set globally to allow specific requests (like large uploads if any) to take longer.
            // DummyJSON is fast, but mobile connections can stall. 30s read/write is sufficient.
            .build()
    }
}
