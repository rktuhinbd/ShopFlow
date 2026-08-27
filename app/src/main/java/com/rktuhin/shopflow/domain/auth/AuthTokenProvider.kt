package com.rktuhin.shopflow.domain.auth

/**
 * An abstraction for providing authorization tokens to the network layer.
 * This is an extension point for future authentication implementation.
 */
interface AuthTokenProvider {
    /**
     * Returns the current access token if one exists, otherwise null.
     */
    fun getAccessToken(): String?
}
