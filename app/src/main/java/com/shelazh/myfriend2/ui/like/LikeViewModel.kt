package com.shelazh.myfriend2.ui.like

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.crocodic.core.api.ApiObserver
import com.shelazh.myfriend2.base.viewModel.BaseViewModel
import com.shelazh.myfriend2.data.local.user.User
import com.shelazh.myfriend2.data.remote.Response2
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LikeViewModel @Inject constructor(): BaseViewModel() {

    private val _friendResponse = MutableSharedFlow<Response2>()
    val friendResponse = _friendResponse.asSharedFlow()

    fun listFriend(id: Int) = viewModelScope.launch {
        ApiObserver.run({apiService.listFriend(id)}, false, object : ApiObserver.ResponseListenerFlow<Response2>(_friendResponse){
            override suspend fun onSuccess(response: Response2) {
                val filterData = response.data.filter { it.userLike == true }
                val filterResponse = Response2(filterData)
                _friendResponse.emit(filterResponse)
            }
        })
    }

    suspend fun getId() = userRepositoryImpl.getUser()?.id
}