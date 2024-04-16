package com.shelazh.myfriend2.ui.register

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.base.activity.CoreActivity
import com.crocodic.core.extension.openActivity
import com.crocodic.core.extension.snacked
import com.shelazh.myfriend2.R
import com.shelazh.myfriend2.databinding.ActivityRegisterBinding
import com.shelazh.myfriend2.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterActivity : CoreActivity<ActivityRegisterBinding, RegisterViewModel>(R.layout.activity_register) {


    var inputName = ""
    var inputPassword = ""
    var inputPhone = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.activity = this
        binding.btnRegister.setOnClickListener{
            validateRegister()
        }

        observe()
    }

    private fun observe(){
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                launch {
                    viewModel.registerResponse.collect{
                        when(it.status){
                            ApiStatus.SUCCESS ->{
                                loadingDialog.dismiss()
                                openActivity<LoginActivity>()
                                finish()
                            }
                            ApiStatus.LOADING ->{
                                loadingDialog.show("Loading...")
                            }
                            else -> {
                                loadingDialog.dismiss()
                                binding.root.snacked("Error")
                            }
                        }

                    }
                }
            }
        }}

    private fun validateRegister(){
        if (inputPhone.isEmpty()){
            binding.inputPhone.error = getString(R.string.error_phone_cannot_be_empty)
            return
        }
        if (inputName.isEmpty()){
            binding.inputName.error = getString(R.string.error_name_cannot_be_empty)
            return
        }
        if (inputPassword.isEmpty()){
            binding.inputPassword.error = getString(R.string.error_password_cannot_be_empty)
            return
        }
        viewModel.register(inputPhone, inputName, inputPassword)
    }
}