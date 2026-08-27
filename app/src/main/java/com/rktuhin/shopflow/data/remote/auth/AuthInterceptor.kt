package com.rktuhin.shopflow.data.remote.auth

import com.rktuhin.shopflow.domain.auth.AuthTokenProvider
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * Intercepts outgoing requests to attach an Authorization header if a token is available.
 */
class AuthInterceptor @Inject constructor(
    private val authTokenProvider: AuthTokenProvider
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = authTokenProvider.getAccessToken()
        
        val request = if (token != null) {
            chain.request().newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }
        
        return chain.proceed(request)
    }
}
