package com.yhjoo.dochef.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.core.app.ActivityCompat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.gson.JsonObject
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import com.yhjoo.dochef.App.Companion.appInstance
import com.yhjoo.dochef.R
import com.yhjoo.dochef.databinding.APostwriteBinding
import com.yhjoo.dochef.utils.RxRetrofitServices.PostService
import com.yhjoo.dochef.utils.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import retrofit2.Response
import java.util.*

class PostWriteActivity : BaseActivity() {
    private val CODE_PERMISSION = 22
    private val IMG_WIDTH = 1080
    private val IMG_HEIGHT = 1080

    enum class MODE {
        WRITE, REVISE
    }

    var binding: APostwriteBinding? = null
    var storageReference: StorageReference? = null
    var postService: PostService? = null
    var mImageUri: Uri? = null
    var current_mode: MODE? = MODE.WRITE
    var userID: String? = null
    var image_url: String? = null
    var postID = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = APostwriteBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        storageReference = FirebaseStorage.getInstance().reference
        postService = RxRetrofitBuilder.create(this, PostService::class.java)
        userID = Utils.getUserBrief(this).userID
        current_mode = intent.getSerializableExtra("MODE") as MODE?
        if (current_mode == MODE.REVISE) {
            postID = intent.getIntExtra("postID", -1)
            binding!!.postwriteToolbar.title = "수정"
            binding!!.postwriteContents.setText(intent.getStringExtra("contents"))
            if (intent.getStringExtra("postImg") != null) {
                Utils.log(intent.getStringExtra("postImg"))
                ImageLoadUtil.loadPostImage(
                    this, intent.getStringExtra("postImg"), binding!!.postwritePostimg
                )
            }
            binding!!.postwriteTags.setTags(intent.getStringArrayExtra("tags"))
        }
        setSupportActionBar(binding!!.postwriteToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding!!.postwritePostimgAdd.setOnClickListener { v: View? -> addImage(v) }
        binding!!.postwriteOk.setOnClickListener { v: View? -> doneClicked(v) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                mImageUri = result.uri
                GlideApp.with(this)
                    .load(mImageUri)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(binding!!.postwritePostimg)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                error.printStackTrace()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CODE_PERMISSION) {
            for (result in grantResults) if (result == PackageManager.PERMISSION_DENIED) {
                appInstance!!.showToast("권한 거부")
                return
            }
            CropImage.activity(mImageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .setRequestedSize(IMG_WIDTH, IMG_HEIGHT)
                .setOutputUri(mImageUri)
                .start(this)
        }
    }

    fun addImage(v: View?) {
        val permissions = arrayOf<String?>(
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        if (Utils.checkPermission(this, permissions)) {
            CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .setMaxCropResultSize(IMG_WIDTH, IMG_HEIGHT)
                .setOutputUri(mImageUri)
                .start(this)
        } else ActivityCompat.requestPermissions(this, permissions, CODE_PERMISSION)
    }

    fun doneClicked(v: View?) {
        val tags = ArrayList(binding!!.postwriteTags.tags)
        if (mImageUri != null) {
            image_url = String.format(
                getString(R.string.format_upload_file),
                userID, java.lang.Long.toString(System.currentTimeMillis())
            )
            progressON(this)
            val ref = storageReference!!.child(getString(R.string.storage_path_post) + image_url)
            ref.putFile(mImageUri!!)
                .addOnSuccessListener { taskSnapshot: UploadTask.TaskSnapshot? ->
                    createORupdatePost(
                        tags
                    )
                }
        } else {
            image_url =
                if (intent.getStringExtra("postImg") != null) intent.getStringExtra("postImg") else ""
            createORupdatePost(tags)
        }
    }

    fun createORupdatePost(tags: ArrayList<String>?) {
        if (current_mode == MODE.WRITE) compositeDisposable!!.add(
            postService!!.createPost(
                userID, image_url,
                binding!!.postwriteContents.text.toString(),
                System.currentTimeMillis(), tags
            )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response: Response<JsonObject?>? ->
                    appInstance!!.showToast("글이 등록되었습니다.")
                    progressOFF()
                    finish()
                }, RxRetrofitBuilder.defaultConsumer())
        ) else compositeDisposable!!.add(
            postService!!.updatePost(
                postID, image_url,
                binding!!.postwriteContents.text.toString(),
                System.currentTimeMillis(), tags
            )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response: Response<JsonObject?>? ->
                    appInstance!!.showToast("업데이트 되었습니다.")
                    progressOFF()
                    finish()
                }, RxRetrofitBuilder.defaultConsumer())
        )
    }
}