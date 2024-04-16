package com.shelazh.myfriend2.data.local.user

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.crocodic.core.data.CoreDao

@Dao
interface UserDao: CoreDao<User> {

    @Query("DELETE FROM User WHERE databaseId = 1")
    suspend fun deleteUser()

    @Query("SELECT * FROM User WHERE databaseId = 1")
    suspend fun checkLogin(): User?

    @Query("SELECT * from User WHERE databaseId = 1 LIMIT 1")
    fun getUser(): LiveData<User?>

    @Query("SELECT * FROM User WHERE name LIKE :keyword")
    suspend fun findUser(keyword: String?): List<User>

    @Query("SELECT * FROM User")
    suspend fun findUser(): List<User>

    suspend fun searchUser(keyword: String? = null): List<User>{
        return if (keyword.isNullOrEmpty()){
            findUser()
        }else{
            findUser("%$keyword%")
        }
    }
}