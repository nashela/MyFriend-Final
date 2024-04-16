package com.shelazh.myfriend2.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.lifecycleScope
import com.crocodic.core.base.activity.NoViewModelActivity
import com.crocodic.core.extension.openActivity
import com.shelazh.myfriend2.R
import com.shelazh.myfriend2.data.repository.user.UserRepository
import com.shelazh.myfriend2.databinding.ActivitySplashBinding
import com.shelazh.myfriend2.ui.home.HomeActivity
import com.shelazh.myfriend2.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : NoViewModelActivity<ActivitySplashBinding>(R.layout.activity_splash) {

    @Inject
    lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Handler(Looper.getMainLooper()).postDelayed({
            lifecycleScope.launch {
                if (userRepository.checkLogin()){
                    openActivity<HomeActivity>()
                }else{
                    openActivity<LoginActivity>()
                }
            }
//            startActivity(Intent(this@SplashActivity, WelcomeActivity::class.java))
            finish()
        }, 2000)
    }
}