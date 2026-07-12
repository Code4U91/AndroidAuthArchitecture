package com.androidautharchitecture.core.network.interceptor

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton


// TEST INTERCEPTOR - for simulating 401 error and checking token refresh
@Singleton
class Debug401Interceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        
        val response = try {
            chain.proceed(request)
        } catch (_: Exception) {
            Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body("{}".toResponseBody("application/json".toMediaType()))
                .build()
        }

        val authHeader = request.header("Authorization") ?: ""
        
        if (request.url.toString().contains("force-401")) {
            //  We check if it is EXACTLY the initial fake token.
            // Refreshed tokens contain a timestamp and the "new-" prefix, so they won't match this.
            if (authHeader == "Bearer fake-jwt-token") {
                Timber.tag("AuthTest").d("Network Level: Found INITIAL token -> Forcing 401")
                return response.newBuilder()
                    .code(401)
                    .message("Unauthorized")
                    .body("{}".toResponseBody("application/json".toMediaType()))
                    .build()
            } else {
                Timber.tag("AuthTest").d("Network Level: Found REFRESHED token ($authHeader) -> Success!")
                return response.newBuilder()
                    .code(200)
                    .message("OK")
                    .body("{\"message\": \"Success!\"}".toResponseBody("application/json".toMediaType()))
                    .build()
            }
        }
        
        return response
    }
}
