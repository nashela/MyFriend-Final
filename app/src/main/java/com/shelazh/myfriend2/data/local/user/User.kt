package com.shelazh.myfriend2.data.local.user

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class User (
    @PrimaryKey(autoGenerate = true)
    @Expose
    @SerializedName("id_database")
    val databaseId: Int = 0,

    @Expose
    @SerializedName("id")
    var id: Int?,

    @Expose
    @SerializedName("phone")
    var phone: String?,

    @Expose
    @SerializedName("name")
    var name: String?,

    @Expose
    @SerializedName("school")
    var school: String?,

    @Expose
    @SerializedName("description")
    var description: String?,

    @Expose
    @SerializedName("photo")
    var photo: String?,

    @Expose
    @SerializedName("like_by_you")
    var userLike: Boolean?
): Parcelable