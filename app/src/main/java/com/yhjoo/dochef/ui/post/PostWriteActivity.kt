package com.yhjoo.dochef.ui.post

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.yhjoo.dochef.*
import com.yhjoo.dochef.data.repository.PostRepository
import com.yhjoo.dochef.databinding.PostwriteActivityBinding
import com.yhjoo.dochef.ui.base.BaseActivity
import com.yhjoo.dochef.utils.*
import kotlinx.coroutines.flow.collect
import java.util.*

class PostWriteActivity : BaseActivity() {
    // TODO
    // textwatcher databinding
    // permission, onactivity databinding

    private val binding: PostwriteActivityBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.postwrite_activity)
    }
    private val postWriteViewModel: PostWriteViewModel by viewModels {
        PostWriteViewModelFactory(
            PostRepository(applicationContext),
            application,
            intent
        )
    }

    private var imageUri: Uri? = null

    private val cropimage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            imageUri = result.uriContent
            GlideApp.with(this)
                .load(imageUri)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(binding.postwritePostimg)
        } else {
            val error = result.error
            error!!.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.postwriteToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.apply {
            lifecycleOwner = this@PostWriteActivity
            viewModel = postWriteViewModel
            activity = this@PostWriteActivity

            OtherUtil.log(postWriteViewModel.currentMode.toString())

            if (postWriteViewModel.currentMode == UIMODE.REVISE) {
                postwriteToolbar.title = "수정"
                postwriteTags.setTags(postWriteViewModel.postInfo!!.tags.toTypedArray())
            }

            postwriteContents.addTextChangedListener(contentsTextWatcher)
        }

        subscribeEventOnLifecycle {
            postWriteViewModel.eventResult.collect {
                when (it.first) {
                    PostWriteViewModel.Events.CREATE_COMPLETE ->
                        App.showToast("글이 등록되었습니다.")
                    PostWriteViewModel.Events.UPDATE_COMPLETE ->
                        App.showToast("업데이트 되었습니다.")
                }

                hideProgress()
                finish()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.PERMISSION_CODE) {
            for (result in grantResults) if (result == PackageManager.PERMISSION_DENIED) {
                App.showToast("권한 거부")
                return
            }
            cropimage.launch(
                options {
                    setGuidelines(CropImageView.Guidelines.ON)
                    setAspectRatio(1, 1)
                    setMaxCropResultSize(
                        Constants.IMAGE.SIZE.POST.IMG_WIDTH,
                        Constants.IMAGE.SIZE.POST.IMG_HEIGHT
                    )
                    setOutputUri(imageUri)
                }
            )
        }
    }

    fun addImage() {
        val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        if (OtherUtil.checkPermission(this, permissions)) {
            cropimage.launch(
                options {
                    setGuidelines(CropImageView.Guidelines.ON)
                    setAspectRatio(1, 1)
                    setMaxCropResultSize(
                        Constants.IMAGE.SIZE.POST.IMG_WIDTH,
                        Constants.IMAGE.SIZE.POST.IMG_HEIGHT
                    )
                    setOutputUri(imageUri)
                }
            )
        } else
            ActivityCompat.requestPermissions(this, permissions, Constants.PERMISSION_CODE)
    }

    fun doneClicked() {
        showProgress(this)

        val tags = ArrayList(binding.postwriteTags.tags)

        postWriteViewModel.uploadPost(
            imageUri,
            binding.postwriteContents.text.toString(),
            tags
        )
    }

    private val contentsTextWatcher: TextWatcher = object : TextWatcher {
        var prevText = ""

        override fun beforeTextChanged(
            s: CharSequence?,
            start: Int,
            count: Int,
            after: Int
        ) {
            prevText = s.toString()
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            if (binding.postwriteContents.lineCount > 3 || s.toString().length >= 120) {
                binding.postwriteContents.setText(prevText)
                binding.postwriteContents.setSelection(prevText.length - 1)
            }
        }
    }

    companion object {
        object UIMODE {
            const val WRITE = 0
            const val REVISE = 1
        }
    }
}