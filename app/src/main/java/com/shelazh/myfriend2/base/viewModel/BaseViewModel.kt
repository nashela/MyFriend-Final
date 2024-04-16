package com.shelazh.myfriend2.base.viewModel

import com.crocodic.core.base.viewmodel.CoreViewModel
import com.shelazh.myfriend2.api.ApiService
import com.shelazh.myfriend2.data.local.user.UserDao
import com.shelazh.myfriend2.data.repository.user.UserRepository
import com.shelazh.myfriend2.data.repository.user.UserRepositoryImpl
import javax.inject.Inject

open class BaseViewModel : CoreViewModel() {

    @Inject
    lateinit var userDao: UserDao

    @Inject
    lateinit var userRepositoryImpl: UserRepositoryImpl

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var apiService: ApiService

    override fun apiLogout() {
        TODO("Not yet implemented")
    }

    override fun apiRenewToken() {
        TODO("Not yet implemented")
    }

}