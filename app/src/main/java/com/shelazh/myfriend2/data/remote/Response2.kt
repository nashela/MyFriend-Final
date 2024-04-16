package com.shelazh.myfriend2.data.remote

import com.crocodic.core.api.ModelResponse
import com.google.gson.annotations.SerializedName
import com.shelazh.myfriend2.data.local.user.User

data class Response2(
    val data: List<User>
): ModelResponse()