package com.shelazh.myfriend2.data.remote

import com.crocodic.core.api.ModelResponse
import com.google.gson.annotations.SerializedName

data class Response3(
    @SerializedName("liked")
    val userLike: Boolean = false
): ModelResponse()
