package com.shelazh.myfriend2.ui

import android.os.Bundle
import com.crocodic.core.base.activity.NoViewModelActivity
import com.crocodic.core.extension.openActivity
import com.shelazh.myfriend2.R
import com.shelazh.myfriend2.databinding.ActivityWelcomeBinding
import com.shelazh.myfriend2.ui.login.LoginActivity

class WelcomeActivity : NoViewModelActivity<ActivityWelcomeBinding>(R.layout.activity_welcome) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.btnNext.setOnClickListener{
            openActivity<LoginActivity>()
            finish()
        }
    }
}