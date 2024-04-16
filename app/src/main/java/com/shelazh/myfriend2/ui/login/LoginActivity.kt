package com.shelazh.myfriend2.ui.login

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.openActivity
import com.shelazh.myfriend2.R
import com.shelazh.myfriend2.base.activity.BaseActivity
import com.shelazh.myfriend2.databinding.ActivityLoginBinding
import com.shelazh.myfriend2.ui.home.HomeActivity
import com.shelazh.myfriend2.ui.register.RegisterActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding, LoginViewModel>(R.layout.activity_login) {

    var inputPhone = ""
    var inputPassword = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.activity = this
        binding.btnLogin.setOnClickListener{
            validateLogin()
        }
        binding.btnRegister.setOnClickListener {
            openActivity<RegisterActivity>()
            finish()
        }

        observe()
    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.loginResponse.collect {
                        if (it.status == ApiStatus.LOADING) {
                            loadingDialog.show(R.string.label_logging_in)
                        } else if (it.status == ApiStatus.SUCCESS) {
                            loadingDialog.dismiss()
                            openActivity<HomeActivity>()
                            finish()
                        }
                    }
                }
            }
        }
    }

    private fun validateLogin() {
        if (inputPhone.isEmpty()) {
            binding.inputPhone.error = getString(R.string.error_phone_cannot_be_empty)
            return
        }

        if (inputPassword.isEmpty()) {
            binding.inputPassword.error = getString(R.string.error_password_cannot_be_empty)
            return
        }

        viewModel.login(inputPhone, inputPassword)
    }
}