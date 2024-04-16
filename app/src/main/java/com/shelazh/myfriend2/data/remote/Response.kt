package com.shelazh.myfriend2.data.remote

import com.crocodic.core.api.ModelResponse
import com.shelazh.myfriend2.data.local.user.User

data class Response (
    val data: User?
): ModelResponse()
