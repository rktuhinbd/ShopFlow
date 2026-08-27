package com.rktuhin.shopflow.di

import com.rktuhin.shopflow.BuildConfig
import com.rktuhin.shopflow.data.remote.auth.AuthInterceptor
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Assert.assertEquals
import org.junit.Test

class NetworkModuleTest {

    @Test
    fun `provideHttpLoggingInterceptor sets BODY level in DEBUG`() {
        val interceptor = NetworkModule.provideHttpLoggingInterceptor()
        
        // Since we are running in a standard unit test, BuildConfig.DEBUG is true by default in debug builds.
        val expectedLevel = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
        
        assertEquals(expectedLevel, interceptor.level)
    }

    @Test
    fun `provideOkHttpClient configures expected timeouts and interceptors in correct order`() {
        val loggingInterceptor = HttpLoggingInterceptor()
        val tokenProvider = object : com.rktuhin.shopflow.domain.auth.AuthTokenProvider {
            override fun getAccessToken(): String? = null
        }
        val authInterceptor = AuthInterceptor(tokenProvider)
        val client = NetworkModule.provideOkHttpClient(authInterceptor, loggingInterceptor)
        
        // Verify Timeouts (30 seconds)
        assertEquals(30_000, client.connectTimeoutMillis)
        assertEquals(30_000, client.readTimeoutMillis)
        assertEquals(30_000, client.writeTimeoutMillis)
        
        // Verify Interceptors and Order: Auth MUST precede Logging
        assertEquals(2, client.interceptors.size)
        assertEquals(authInterceptor, client.interceptors[0])
        assertEquals(loggingInterceptor, client.interceptors[1])
    }
}
