package com.shelazh.myfriend2.ui.profile

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.openActivity
import com.crocodic.core.extension.openCamera
import com.crocodic.core.extension.openGallery
import com.crocodic.core.extension.snacked
import com.crocodic.core.extension.text
import com.crocodic.core.extension.textOf
import com.crocodic.core.helper.DateTimeHelper
import com.crocodic.core.helper.StringHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.shelazh.myfriend2.R
import com.shelazh.myfriend2.base.activity.BaseActivity
import com.shelazh.myfriend2.data.local.user.User
import com.shelazh.myfriend2.data.repository.user.UserRepository
import com.shelazh.myfriend2.databinding.ActivityProfileBinding
import com.shelazh.myfriend2.ui.login.LoginActivity
import com.shelazh.myfriend2.util.binding.ImageViewBindingAdapter.imageBitmap
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject

@AndroidEntryPoint
class ProfileActivity :
    BaseActivity<ActivityProfileBinding, ProfileViewModel>(R.layout.activity_profile) {

    @Inject
    lateinit var userRepository: UserRepository

    private var user: User? = null
    private var filePhoto: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observe()

        binding.imgBack.setOnClickListener {
            onBackPressed()
        }
        binding.btnSave.setOnClickListener {
            validateForm()
        }
        binding.imgPhotoProfile.setOnClickListener {
            openPictureDialog()
        }
        binding.btnLogout.setOnClickListener {
            lifecycleScope.launch {
                userRepository.deleteUser()
                openActivity<LoginActivity>()
                finishAffinity()
            }
        }
    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.updateProfileResponse.collect {
                        when (it.status) {
                            ApiStatus.SUCCESS -> {
                                binding.root.snacked("Berhasil update profile")
                                loadingDialog.dismiss()
                                finish()
                            }

                            ApiStatus.LOADING -> {
                                loadingDialog.show("Loading...")
                            }

                            else -> {
                                loadingDialog.dismiss()
                                binding.root.snacked("Ada masalah saat mengupdate profil, silahkan coba lagi")
                            }
                        }
                    }
                }

                launch {
                    user = viewModel.getUser()
                    if (user != null) {
                        binding.etNameEdit.text(user!!.name)
                        binding.etSchoolEdit.text(user!!.school)
                        binding.etBioEdit.text(user!!.description)
                        val avatar = StringHelper.generateTextDrawable(
                            StringHelper.getInitial(user!!.name?.trim()),
                            ContextCompat.getColor(this@ProfileActivity, R.color.yellow_splash),
                            binding.imgPhotoProfile.measuredWidth
                        )
                        if (user!!.photo.isNullOrEmpty()) {
                            binding.imgPhotoProfile.setImageDrawable(avatar)
                        } else {
                            val requestOption = RequestOptions().placeholder(avatar).circleCrop()
                            Glide
                                .with(this@ProfileActivity)
                                .load(StringHelper.validateEmpty(user!!.photo))
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .apply(requestOption)
                                .error(avatar)
                                .into(binding.imgPhotoProfile)
                        }
                    }


                }
            }
        }

    }
    private fun openPictureDialog(){
        MaterialAlertDialogBuilder(this).apply {
            setItems(R.array.label_image_source){ dialog, which ->
                when(which){
                    0 -> activityLauncher.openCamera(
                        this@ProfileActivity,
                        "$packageName.fileprovider"
                    ){ file, e ->
                        filePhoto = file
                        displayImageFromFile(file, binding.imgPhotoProfile)
                    }
                    1 -> activityLauncher.openGallery(this@ProfileActivity){ file, e ->
                        filePhoto = file
                        displayImageFromFile(file, binding.imgPhotoProfile)
                    }
                }
            }
        }.create().show()
    }

    private fun displayImageFromFile(imageFile: File?, imageView: ImageView){
        if (imageFile == null) return
        val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)

        imageView.imageBitmap(bitmap)
    }

    private fun checkGallery() {
        if (checkPermissionGallery()) {
            openGalleryResult()
        } else {
            requestPermissionGallery()
        }
    }

    private fun validateForm() {
        val name = binding.etNameEdit.textOf()
        val school = binding.etSchoolEdit.textOf()
        val description = binding.etBioEdit.textOf()


        val oldName = user?.name ?: ""
        val oldSchool = user?.school?: ""
        val oldBio = user?.description?: ""

        if (filePhoto == null) {
            if (name.isEmpty() || school.isEmpty() || description.isEmpty()) {
                binding.root.snacked("Tidak boleh kosong")
                return
            }
            if (name == oldName && school == oldSchool && description == oldBio) {
                binding.root.snacked("Tidak ada perubahan data")
                return
            }
            viewModel.updateProfileName(name, school, description)
        } else {
            viewModel.updateProfile(name, school, description, filePhoto!!)
        }
    }

    private fun getNewFileName(isPdf: Boolean = false): String {
        val timestamp = DateTimeHelper().createAtLong().toString()
        return if (isPdf) "pdf_${timestamp}_.pdf" else "JPEG_${timestamp}_.jpg"
    }

    private fun getAppSpecificAlbumStorageDir(albumName: String, subAlbumName: String): File {
        val file = File(getExternalFilesDir(albumName), subAlbumName)
        if (!file.mkdirs()) {
        }
        return file
    }

    @SuppressLint("New Api")
    private fun getOrientation3(inputStream: InputStream?): Int {
        val exif: ExifInterface
        var orientation = -1
        inputStream?.let {
            try {
                exif = ExifInterface(it)
                orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return orientation
    }

    @Throws(IOException::class)
    private fun onCreateImageFile(): File {
        val timestamp = DateTimeHelper().createAtLong().toString()
        val storageDir =
            getAppSpecificAlbumStorageDir(Environment.DIRECTORY_DOCUMENTS, "Attachment")
        return File.createTempFile(
            "JPEG_${timestamp}_",
            ".jpg",
            storageDir
        )
    }

    @SuppressLint("Range")
    private fun getOrientation(shareUri: Uri): Int {
        val orientationColumn = arrayOf(MediaStore.Images.Media.ORIENTATION)
        val cur = contentResolver.query(
            shareUri,
            orientationColumn,
            null,
            null,
            null
        )
        var orientation = -1
        if (cur != null && cur.moveToFirst()) {
            if (cur.columnCount > 0) {
                orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]))
            }
            cur.close()
        }
        return orientation
    }

    @SuppressLint("NewApi")
    private fun getOrientation2(shareUri: Uri): Int {
        val inputStream = contentResolver.openInputStream(shareUri)
        return getOrientation3(inputStream)
    }

    private fun generateFileImage(uri: Uri) {
        try {
            val parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r")
            val fileDescriptor = parcelFileDescriptor?.fileDescriptor
            val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor?.close()

            val orientation = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                getOrientation2(uri)
            } else {
                getOrientation(uri)
            }

            val file = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                onCreateImageFile()
            } else {
                File(externalCacheDir?.absolutePath, getNewFileName())
            }

            val fos = FileOutputStream(file)
            var bitmap = image

            if (orientation != -1 && orientation != 0) {
                val matrix = Matrix()
                when (orientation) {
                    6 -> matrix.postRotate(90f)
                    3 -> matrix.postRotate(180f)
                    8 -> matrix.postRotate(270f)
                    else -> matrix.postRotate(orientation.toFloat())
                }
                bitmap =
                    Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            }

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            Glide
                .with(this)
                .load(bitmap)
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(RequestOptions.circleCropTransform())
                .into(binding.imgPhotoProfile)

            filePhoto = file
        } catch (e: Exception) {
            e.printStackTrace()
            binding.root.snacked("File ini tidak digunakan")
        }
    }

    private fun checkPermissionGallery(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun openGalleryResult() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activityLauncher.launch(galleryIntent) { activityResult ->
            activityResult.data?.data?.let {
                generateFileImage(it)
            }
        }
    }

    private fun requestPermissionGallery() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
            110
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 110) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGalleryResult()
                binding.root.snacked("Permission Granted")
            } else {
                binding.root.snacked("Permission Denied")

            }
        }
    }
}