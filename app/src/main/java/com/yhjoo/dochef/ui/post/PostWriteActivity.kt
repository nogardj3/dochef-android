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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.yhjoo.dochef.*
import com.yhjoo.dochef.data.network.RetrofitBuilder
import com.yhjoo.dochef.data.repository.PostRepository
import com.yhjoo.dochef.databinding.PostwriteActivityBinding
import com.yhjoo.dochef.ui.base.BaseActivity
import com.yhjoo.dochef.ui.recipe.RecipeMakeActivity
import com.yhjoo.dochef.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class PostWriteActivity : BaseActivity() {
    companion object CONSTANTS {
        object UIMODE {
            const val WRITE = 0
            const val REVISE = 1
        }
    }

    private val binding: PostwriteActivityBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.postwrite_activity)
    }
    private val postWriteViewModel: PostWriteViewModel by viewModels {
        PostWriteViewModelFactory(
            application,
            intent,
            PostRepository(applicationContext)
        )
    }

    private lateinit var storageReference: StorageReference
    private lateinit var userID: String
    private var postID = 0

    private var imageUri: Uri? = null
    private var imageString: String? = null
    private var currentMode = UIMODE.WRITE

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

        storageReference = FirebaseStorage.getInstance().reference
        userID = DatastoreUtil.getUserBrief(this).userID
        currentMode = intent.getIntExtra("MODE", UIMODE.WRITE)

        if (currentMode == UIMODE.REVISE) {
            postID = intent.getIntExtra("postID", -1)

            binding.apply {

                postwriteToolbar.title = "수정"
                postwriteContents.setText(intent.getStringExtra("contents"))
                if (intent.getStringExtra("postImg") != null) {
                    ImageLoaderUtil.loadPostImage(
                        this@PostWriteActivity, intent.getStringExtra("postImg")!!, postwritePostimg
                    )
                }
                postwriteTags.setTags(intent.getStringArrayExtra("tags"))
            }
        }
        setSupportActionBar(binding.postwriteToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.apply {
            lifecycleOwner = this@PostWriteActivity

            postwriteContents.addTextChangedListener(object : TextWatcher {
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
            })
            postwritePostimgAdd.setOnClickListener { addImage() }
            postwriteOk.setOnClickListener { doneClicked() }

            postWriteViewModel.isFinished.observe(this@PostWriteActivity, {
                if (it) {
                    App.showToast(
                        if (currentMode == RecipeMakeActivity.MODE.REVISE)
                            "업데이트 되었습니다."
                        else
                            "글이 등록되었습니다."
                    )

                    hideProgress()
                    finish()
                }
            })
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

    private fun addImage() {
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

    private fun doneClicked() {
        val tags = ArrayList(binding.postwriteTags.tags)
        if (imageUri != null) {
            imageString = String.format(
                getString(R.string.format_upload_file),
                userID,
                System.currentTimeMillis().toString()
            )
            showProgress(this)
            val ref = storageReference.child(getString(R.string.storage_path_post) + imageString)
            ref.putFile(imageUri!!)
                .addOnSuccessListener {
                    createORupdatePost(tags)
                }
        } else {
            imageString =
                if (intent.getStringExtra("postImg") != null) intent.getStringExtra("postImg")
                else ""
            createORupdatePost(tags)
        }
    }

    private fun createORupdatePost(tags: ArrayList<String>) =
        CoroutineScope(Dispatchers.Main).launch {
            runCatching {
                if (currentMode == UIMODE.WRITE) {
                    postWriteViewModel.createPost(
                        imageString!!,
                        binding.postwriteContents.text.toString(),
                        tags
                    )

                } else
                    postWriteViewModel.updatePost(
                        imageString!!,
                        binding.postwriteContents.text.toString(),
                        tags
                    )
            }
                .onSuccess { }
                .onFailure {
                    RetrofitBuilder.defaultErrorHandler(it)
                }
        }
}