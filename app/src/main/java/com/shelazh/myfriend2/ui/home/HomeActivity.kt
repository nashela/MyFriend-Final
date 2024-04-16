package com.shelazh.myfriend2.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.crocodic.core.base.adapter.ReactiveListAdapter
import com.crocodic.core.extension.openActivity
import com.shelazh.myfriend2.R
import com.shelazh.myfriend2.base.activity.BaseActivity
import com.shelazh.myfriend2.data.local.user.User
import com.shelazh.myfriend2.data.repository.user.UserRepository
import com.shelazh.myfriend2.databinding.ActivityHomeBinding
import com.shelazh.myfriend2.databinding.ItemFriendBinding
import com.shelazh.myfriend2.ui.detail.DetailActivity
import com.shelazh.myfriend2.ui.like.LikeActivity
import com.shelazh.myfriend2.ui.profile.ProfileActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityHomeBinding, HomeViewModel>(R.layout.activity_home) {

    private val rotateOpen: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.rotate_open_anim
        )
    }
    private val rotateClose: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.rotate_close_anim
        )
    }
    private val fromBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.from_bottom_anim
        )
    }
    private val toBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.to_bottom_anim
        )
    }

    private var clicked = false

    private val keyword: String? = null

    @Inject
    lateinit var userRepository: UserRepository

//    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.access = this

//        binding.recycler.adapter = adapter

        observe()

        binding.searchView.doAfterTextChanged { editable ->
            val keyword = editable?.toString()?.trim()?: ""
            viewModel.getFriendByQuery(keyword)
        }

        binding.btnProfile.setOnClickListener {
            openActivity<ProfileActivity>()
        }
        binding.btnFavorite.setOnClickListener {
            openActivity<LikeActivity>()
        }

        binding.btnList.setOnClickListener {
            onAddButtonClick()
        }
    }

    private fun getFriend() = lifecycleScope.launch(Dispatchers.IO){
        viewModel.getId().let {
            viewModel.listFriend(it!!)
        }
    }

    private fun observe() {
        val adapter =
            ReactiveListAdapter<ItemFriendBinding, User>(R.layout.item_friend).initItem { position, data ->
                toDetail(data)
        }
        lifecycleScope.launch {
            launch {
                viewModel.friendResponse.collect {
                    adapter.submitList(it.data)
                }
            }
            launch {
                userRepository.getUser()?.let {
                    val greetingText = "Hello, ${it.name}"
                    binding.tvName.text = greetingText
                    binding.tvName.setTextColor(Color.BLACK)
                }
            }

            binding.recycler.adapter = adapter

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

    private fun onAddButtonClick() {
        setVisibility(clicked)
        setAnimation(clicked)
        clicked = !clicked
    }

    private fun setVisibility(clicked: Boolean) {
        if (!clicked) {
            binding.btnFavorite.visibility = View.VISIBLE
            binding.btnProfile.visibility = View.VISIBLE
        } else {
            binding.btnFavorite.visibility = View.INVISIBLE
            binding.btnProfile.visibility = View.INVISIBLE
        }
    }

    private fun setAnimation(clicked: Boolean) {
        if (!clicked) {
            binding.btnFavorite.startAnimation(fromBottom)
            binding.btnProfile.startAnimation(fromBottom)
            binding.btnList.startAnimation(rotateOpen)
        } else {
            binding.btnFavorite.startAnimation(toBottom)
            binding.btnProfile.startAnimation(toBottom)
            binding.btnList.startAnimation(rotateClose)
        }
    }

    override fun onStart() {
        super.onStart()
        getFriend()
    }
}
