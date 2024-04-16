package com.shelazh.myfriend2.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.shelazh.myfriend2.data.local.user.User
import com.shelazh.myfriend2.data.local.user.UserDao

@Database(
    entities = [User::class],
    version = 1,
    exportSchema = true
)
abstract class MyDatabase: RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object {

        @Volatile
        private var INSTANCE: MyDatabase? = null

        @Synchronized
        fun getDatabase(context: Context): MyDatabase {
            val tmpInstance = INSTANCE
            if(tmpInstance == null) {
                INSTANCE = Room.databaseBuilder(context.applicationContext, MyDatabase::class.java, "my_database")
                    .fallbackToDestructiveMigration()
                    .build()
            }

            return INSTANCE!!
        }
    }
}