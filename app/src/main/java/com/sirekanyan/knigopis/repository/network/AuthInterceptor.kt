package com.sirekanyan.knigopis.repository.network

import com.sirekanyan.knigopis.repository.TokenStorage
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Response

private const val UNAUTHORIZED_HTTP_CODE = 401
private const val ACCESS_TOKEN_PARAMETER_NAME = "access-token"

class AuthInterceptor(private val storage: TokenStorage) : Interceptor {

    override fun intercept(chain: Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        if (response.code() == UNAUTHORIZED_HTTP_CODE) {
            storage.accessToken?.let { accessToken ->
                val urlWithAccessToken = request.url().newBuilder()
                    .addQueryParameter(ACCESS_TOKEN_PARAMETER_NAME, accessToken)
                    .build()
                response.close()
                return chain.proceed(
                    request.newBuilder()
                        .url(urlWithAccessToken)
                        .build()
                )
            }
        }
        return response
    }

}