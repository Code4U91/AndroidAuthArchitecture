package com.androidautharchitecture.core.result

import com.androidautharchitecture.app.session.SessionManager

/**
 * Clears the locally stored user session if this result represents an
 * [AppError.Unauthorized] failure.
 *
 * Use this only after authenticated API requests (endpoints that require a
 * valid access token).
 *
 * Do NOT use this for public endpoints such as:
 * - Login
 * - Register
 * - Forgot Password
 * - Verify OTP
 *
 * A 401 from an authenticated endpoint indicates that the current session
 * is no longer valid (for example, the token expired, was revoked, or is
 * otherwise no longer accepted by the backend). In that case, the local
 * session is invalidated so the application can return the user to the
 * authentication flow.
 *
 * This extension intentionally does not modify the returned [AppResult];
 * it performs the side effect of clearing the local session and then
 * returns the original result unchanged.
 */
suspend fun <T> AppResult<T>.clearSessionIfUnauthorized(
    sessionManager: SessionManager
): AppResult<T> {

    if(this is AppResult.Failure &&
        error == AppError.Unauthorized
    ) {
        sessionManager.clearSession()
    }

    return  this
}

