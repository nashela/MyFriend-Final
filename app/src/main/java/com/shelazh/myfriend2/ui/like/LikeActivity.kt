package com.shelazh.myfriend2.ui.like

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.crocodic.core.base.adapter.ReactiveListAdapter
import com.crocodic.core.extension.openActivity
import com.shelazh.myfriend2.R
import com.shelazh.myfriend2.base.activity.BaseActivity
import com.shelazh.myfriend2.data.local.user.User
import com.shelazh.myfriend2.databinding.ActivityLikeBinding
import com.shelazh.myfriend2.databinding.ItemFriendBinding
import com.shelazh.myfriend2.ui.detail.DetailActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LikeActivity : BaseActivity<ActivityLikeBinding, LikeViewModel>(R.layout.activity_like) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val adapter = ReactiveListAdapter<ItemFriendBinding, User>(R.layout.item_friend).initItem{position, data ->
            toDetail(data)
        }
        binding.adapter = adapter

        observe()
        getFriend()
    }
    private fun observe(){
        lifecycleScope.launch {
//            launch {
//                viewModel.getId().let {
//                    viewModel.listFriend(it!!)
//                }
//            }
                viewModel.friendResponse.collect{
                    binding.adapter?.submitList(it.data)
                }
        }
    }
    private fun getFriend() = lifecycleScope.launch(Dispatchers.IO){
        viewModel.getId().let {
            viewModel.listFriend(it!!)
        }
    }

    private fun toDetail(data: User) {
        openActivity<DetailActivity> {
            putExtra(DetailActivity.DATA, data)
            putExtra("id", data.id)
            putExtra("name", data.name)
            putExtra("school", data.school)
            putExtra("description", data.description)
            putExtra("userLike", data.userLike)
//            putExtra("photo", data.photo)
        }
    }

//    override fun onStart() {
//        super.onStart()
//        observe()
//    }
}

