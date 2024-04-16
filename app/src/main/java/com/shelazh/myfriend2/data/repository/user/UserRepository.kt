package com.shelazh.myfriend2.data.repository.user

import com.shelazh.myfriend2.data.local.user.User

interface UserRepository {
    suspend fun saveUSer(user: User)

    suspend fun searchFriend(keyword: String?): List<User>

    suspend fun deleteUser()

    suspend fun checkLogin(): Boolean

    suspend fun getUser(): User?
}