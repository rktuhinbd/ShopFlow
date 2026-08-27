package com.rktuhin.shopflow.data.remote.auth

import com.rktuhin.shopflow.domain.auth.AuthTokenProvider
import okhttp3.Call
import okhttp3.Connection
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.concurrent.TimeUnit

class AuthInterceptorTest {

    private fun createChain(request: Request): Interceptor.Chain {
        return object : Interceptor.Chain {
            override fun request(): Request = request
            override fun proceed(request: Request): Response {
                // Return a dummy response reflecting the request so we can inspect it
                return Response.Builder()
                    .request(request)
                    .protocol(Protocol.HTTP_1_1)
                    .code(200)
                    .message("OK")
                    .build()
            }

            override fun call(): Call = throw NotImplementedError()
            override fun connectTimeoutMillis(): Int = 0
            override fun connection(): Connection? = null
            override fun readTimeoutMillis(): Int = 0
            override fun withConnectTimeout(timeout: Int, unit: TimeUnit): Interceptor.Chain = this
            override fun withReadTimeout(timeout: Int, unit: TimeUnit): Interceptor.Chain = this
            override fun withWriteTimeout(timeout: Int, unit: TimeUnit): Interceptor.Chain = this
            override fun writeTimeoutMillis(): Int = 0
        }
    }

    @Test
    fun `intercept adds Authorization header when token is available`() {
        val tokenProvider = object : AuthTokenProvider {
            override fun getAccessToken(): String = "test_token_123"
        }
        val interceptor = AuthInterceptor(tokenProvider)
        
        val initialRequest = Request.Builder().url("https://example.com").build()
        val chain = createChain(initialRequest)
        
        val response = interceptor.intercept(chain)
        
        assertEquals("Bearer test_token_123", response.request.header("Authorization"))
    }

    @Test
    fun `intercept does not add Authorization header when token is null`() {
        val tokenProvider = object : AuthTokenProvider {
            override fun getAccessToken(): String? = null
        }
        val interceptor = AuthInterceptor(tokenProvider)
        
        val initialRequest = Request.Builder().url("https://example.com").build()
        val chain = createChain(initialRequest)
        
        val response = interceptor.intercept(chain)
        
        assertNull(response.request.header("Authorization"))
    }
}
