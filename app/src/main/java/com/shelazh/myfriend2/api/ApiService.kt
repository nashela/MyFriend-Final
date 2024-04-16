package com.shelazh.myfriend2.api

import com.shelazh.myfriend2.data.remote.Response
import com.shelazh.myfriend2.data.remote.Response2
import com.shelazh.myfriend2.data.remote.Response3
import okhttp3.MultipartBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("phone") phone : String?,
        @Field("password") password : String?
    ): Response

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("phone") phone: String?,
        @Field("name") name: String?,
        @Field("password") password: String?
    ): Response

    @FormUrlEncoded
    @POST("update-profile")
    suspend fun updateProfileNoPhoto(
        @Field("id_user") idUSer: Int?,
        @Field("name") name: String?,
        @Field("school") school: String?,
        @Field("description") description: String?
    ): Response

    @Multipart
    @POST("update-profile")
    suspend fun updateProfileWithPhoto(
        @Part("id_user") idUSer: Int?,
        @Part("name") name: String?,
        @Part("school") school: String?,
        @Part("description") description: String?,
        @Part photo: MultipartBody.Part?
    ): Response

    @GET("get-list-friends")
    suspend fun listFriend(
        @Query ("users_id") idUSer: Int?
    ): Response2

    @FormUrlEncoded
    @POST("like")
    suspend fun like(
        @Field("users_id") idUser: Int?,
        @Field("user_id_i_like") userLike: Int?
    ): Response3
}