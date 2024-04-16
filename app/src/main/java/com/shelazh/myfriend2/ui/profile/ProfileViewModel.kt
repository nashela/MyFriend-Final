package com.shelazh.myfriend2.ui.profile

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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(): BaseViewModel() {

    suspend fun getUser() = userRepository.getUser()

    private val _updateProfileResponse = MutableSharedFlow<Response>()
    val updateProfileResponse = _updateProfileResponse.asSharedFlow()

    fun updateProfileName(name: String, school: String, description: String){
//        _updateProfileResponse.postValue(ApiResponse(ApiStatus.LOADING, "Loading..."))
        viewModelScope.launch {
            ApiObserver.run({ apiService.updateProfileNoPhoto(getUser()?.id?:0, name, school, description) }, false, object : ApiObserver.ResponseListenerFlow<Response>(_updateProfileResponse){
                override suspend fun onSuccess(response: Response) {
//                    val apiMessage = response.getString("message")
//                    val data = response.getJSONObject("data").toObject<User>(gson)
//                    userDao.insert(data.copy(databaseId = 1))
                    response.data?.let {
                        userRepository.saveUSer(it.copy(databaseId = 1))
                    }
                    _updateProfileResponse.emit(response)
                }
            })
        }
    }

    fun updateProfile(name: String, school: String, description: String, photo: File){
        val fileBody = photo.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val filePart = MultipartBody.Part.createFormData("photo", photo.name, fileBody)
//        _updateProfileResponse.postValue(ApiResponse(ApiStatus.LOADING, "Loading..."))
        viewModelScope.launch {
            ApiObserver.run({ apiService.updateProfileWithPhoto(getUser()?.id?:0, name, school, description, filePart) }, false, object : ApiObserver.ResponseListenerFlow<Response>(_updateProfileResponse){
                override suspend fun onSuccess(response: Response) {
//                    val apiMessage = response.getString("message")
//                    val data = response.getJSONObject("data").toObject<User>(gson)
//                    userDao.insert(data.copy(databaseId = 1))
                    response.data?.let {
                        userRepository.saveUSer(it.copy(databaseId = 1))
                    }
                    _updateProfileResponse.emit(response)

                }
            })
        }

    }
}