package com.shelazh.myfriend2.ui.home

import androidx.lifecycle.viewModelScope
import com.crocodic.core.api.ApiObserver
import com.shelazh.myfriend2.api.ApiService
import com.shelazh.myfriend2.base.viewModel.BaseViewModel
import com.shelazh.myfriend2.data.local.user.User
import com.shelazh.myfriend2.data.remote.Response2
import com.shelazh.myfriend2.data.repository.user.UserRepository
import com.shelazh.myfriend2.data.repository.user.UserRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(): BaseViewModel(){

    val user = MutableSharedFlow<List<User>>()

//    fun getAllFriends(keyword: String? = null) = viewModelScope.launch {
//        user.emit(userRepository.searchFriend(keyword))
//    }

    fun getFriendByQuery(keyword: String) = viewModelScope.launch {
        ApiObserver.run({apiService.listFriend(getId())}, false, object : ApiObserver.ResponseListenerFlow<Response2>(_friendResponse){
            override suspend fun onSuccess(response: Response2) {
                val filterData = response.data.filter { it.name == keyword }
                val filterResponse = Response2(filterData)
                _friendResponse.emit(filterResponse)
            }
        })
    }

    private val _friendResponse = MutableSharedFlow<Response2>()
    val friendResponse = _friendResponse.asSharedFlow()

    fun listFriend(id: Int) = viewModelScope.launch {
        ApiObserver.run({ apiService.listFriend(id)}, false, object : ApiObserver.ResponseListenerFlow<Response2>(_friendResponse){
            override suspend fun onSuccess(response: Response2) {
                _friendResponse.emit(response)
            }
        })
    }
    suspend fun getId() = userRepositoryImpl.getUser()?.id
}
