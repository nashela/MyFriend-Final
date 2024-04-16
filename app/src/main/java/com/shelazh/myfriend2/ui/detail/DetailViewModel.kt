package com.shelazh.myfriend2.ui.detail

import androidx.lifecycle.viewModelScope
import com.crocodic.core.api.ApiObserver
import com.shelazh.myfriend2.api.ApiService
import com.shelazh.myfriend2.base.viewModel.BaseViewModel
import com.shelazh.myfriend2.data.local.user.User
import com.shelazh.myfriend2.data.remote.Response
import com.shelazh.myfriend2.data.remote.Response3
import com.shelazh.myfriend2.data.repository.user.UserRepository
import com.shelazh.myfriend2.data.repository.user.UserRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(): BaseViewModel() {

    private val _like = MutableSharedFlow<Response3>()
    val like = _like.asSharedFlow()

    fun likes(id: Int?, userLike: Int?) = viewModelScope.launch {
        ApiObserver.run ({ apiService.like(id, userLike) }, false, object : ApiObserver.ResponseListenerFlow<Response3>(_like){
            override suspend fun onSuccess(response: Response3) {
                _like.emit(response)
            }
        })
    }
    suspend fun getId() = userRepositoryImpl.getUser()?.id
}