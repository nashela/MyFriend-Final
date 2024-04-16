package com.shelazh.myfriend2.ui.login

import androidx.lifecycle.viewModelScope
import com.crocodic.core.api.ApiObserver
import com.shelazh.myfriend2.api.ApiService
import com.shelazh.myfriend2.base.viewModel.BaseViewModel
import com.shelazh.myfriend2.data.remote.Response
import com.shelazh.myfriend2.data.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : BaseViewModel() {

    private val _loginResponse = MutableSharedFlow<Response>()
    val loginResponse = _loginResponse.asSharedFlow()

    fun login(phone: String?, password: String?) = viewModelScope.launch {
        ApiObserver.run( { apiService.login(phone, password)}, false, object : ApiObserver.ResponseListenerFlow<Response>(_loginResponse){
            override suspend fun onSuccess(response: Response) {
                super.onSuccess(response)
                response.data?.let {
                    userRepository.saveUSer(it)
                }
            }
        })
    }
}