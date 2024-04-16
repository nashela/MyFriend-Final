package com.shelazh.myfriend2.ui.register

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.crocodic.core.api.ApiObserver
import com.shelazh.myfriend2.api.ApiService
import com.shelazh.myfriend2.base.viewModel.BaseViewModel
import com.shelazh.myfriend2.data.remote.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor() : BaseViewModel() {

    private val _registerResponse = MutableSharedFlow<Response>()
    val registerResponse = _registerResponse.asSharedFlow()

    val message = MutableLiveData<String>()

    fun register(phone: String?, name: String?, password: String?) =
        viewModelScope.launch {
            try {
                ApiObserver.run(
                    { apiService.register(phone, name, password) },
                    false,
                    object : ApiObserver.ResponseListenerFlow<Response>(_registerResponse) {
                        override suspend fun onSuccess(response: Response) {
                            _registerResponse.emit(response)
                        }

                    }
                )
            }catch (e: Exception){
                val error = "The phone has already been taken"
                message.value = error
            }
        }
}