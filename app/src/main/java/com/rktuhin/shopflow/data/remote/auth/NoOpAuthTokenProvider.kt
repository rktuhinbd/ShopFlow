package com.rktuhin.shopflow.data.remote.auth

import com.rktuhin.shopflow.domain.auth.AuthTokenProvider
import javax.inject.Inject

/**
 * A no-op implementation of [AuthTokenProvider] that always returns null.
 * Used when authentication is not required (e.g., public APIs like DummyJSON).
 */
class NoOpAuthTokenProvider @Inject constructor() : AuthTokenProvider {
    override fun getAccessToken(): String? = null
}
