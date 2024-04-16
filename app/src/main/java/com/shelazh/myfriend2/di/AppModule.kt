package com.shelazh.myfriend2.di

import android.content.Context
import com.crocodic.core.helper.NetworkHelper
import com.shelazh.myfriend2.api.ApiService
import com.shelazh.myfriend2.data.MyDatabase
import com.shelazh.myfriend2.data.repository.user.UserRepository
import com.shelazh.myfriend2.data.repository.user.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context) = MyDatabase.getDatabase(context)

    @Singleton
    @Provides
    fun provideUserDao(database: MyDatabase) = database.userDao()

    @Singleton
    @Provides
    fun provideApiService(): ApiService {
        return NetworkHelper.provideApiService(
            baseUrl = "https://neptune74.crocodic.net/myfriend-kelasindustri/public/api/",
            okHttpClient = NetworkHelper.provideOkHttpClient(),
            converterFactory = listOf(GsonConverterFactory.create())
        )
    }

    @InstallIn(SingletonComponent::class)
    @Module
    abstract class RepositoryModule{

        @Singleton
        @Binds
        abstract fun bindUSerRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository
    }
}