package com.shelazh.myfriend2.ui.detail

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.crocodic.core.extension.parcelable
import com.crocodic.core.extension.tos
import com.shelazh.myfriend2.R
import com.shelazh.myfriend2.base.activity.BaseActivity
import com.shelazh.myfriend2.data.local.user.User
import com.shelazh.myfriend2.databinding.ActivityDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailActivity : BaseActivity<ActivityDetailBinding, DetailViewModel>(R.layout.activity_detail) {

//    private var user: User? = null

    var id = 1
    var name = ""
    var school = ""
    var description = ""

    var isLiked = false
//    var photo = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observe()

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
        binding.btnLike.setOnClickListener {
            like()
//            tos("Test")
        }

        binding.activity = this

        id = intent.getIntExtra("id", 1)
        name = intent.getStringExtra("name") ?: ""
        school = intent.getStringExtra("school") ?: ""
        description = intent.getStringExtra("description") ?: ""
        isLiked = intent.getBooleanExtra("userLike", false)
//        photo = intent.getStringExtra("photo") ?: ""

        val data = intent.parcelable<User>(DATA)
        binding.user = data

        if (isLiked){
            binding.btnLike.setImageResource(R.drawable.baseline_favorite2)
        } else{
            binding.btnLike.setImageResource(R.drawable.baseline_favorite)
        }
    }

    private fun like(){
        lifecycleScope.launch {
            viewModel.likes(viewModel.getId() ?: return@launch, id)
        }
    }

    private fun observe(){
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                launch {
                    viewModel.like.collect{
                        it.let {
                            isLiked = it.userLike
                            if (isLiked){
                                binding.btnLike.setImageResource(R.drawable.baseline_favorite2)
                            } else{
                                binding.btnLike.setImageResource(R.drawable.baseline_favorite)
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val DATA = "data"
    }

}