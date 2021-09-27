package com.yhjoo.dochef.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.getInputField
import com.afollestad.materialdialogs.input.input
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.yhjoo.dochef.App
import com.yhjoo.dochef.GlideApp
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.DataGenerator
import com.yhjoo.dochef.data.model.Recipe
import com.yhjoo.dochef.data.model.UserDetail
import com.yhjoo.dochef.data.network.RetrofitBuilder
import com.yhjoo.dochef.data.network.RetrofitServices.*
import com.yhjoo.dochef.data.repository.PostRepository
import com.yhjoo.dochef.databinding.HomeActivityBinding
import com.yhjoo.dochef.ui.common.adapter.RecipeHorizontalHomeAdapter
import com.yhjoo.dochef.ui.base.BaseActivity
import com.yhjoo.dochef.ui.follow.FollowListActivity
import com.yhjoo.dochef.ui.post.PostDetailActivity
import com.yhjoo.dochef.ui.recipe.RecipeDetailActivity
import com.yhjoo.dochef.ui.recipe.RecipeMyListActivity
import com.yhjoo.dochef.ui.common.viewmodel.PostListViewModel
import com.yhjoo.dochef.ui.common.viewmodel.PostListViewModelFactory
import com.yhjoo.dochef.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class HomeActivity : BaseActivity() {
    companion object {
        const val CODE_PERMISSION = 22
        const val IMG_WIDTH = 360
        const val IMG_HEIGHT = 360
    }

    object UIMODE {
        const val OWNER = 0
        const val OTHERS = 1
    }

    object OPERATION {
        const val VIEW = 0
        const val REVISE = 1
    }

    private val binding: HomeActivityBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.home_activity)
    }
    private lateinit var postListViewModel: PostListViewModel

    private lateinit var postListAdapter: PostListAdapter

    private lateinit var storageReference: StorageReference
    private lateinit var accountService: AccountService
    private lateinit var userService: UserService
    private lateinit var recipeService: RecipeService

    private lateinit var recipeHorizontalHomeAdapter: RecipeHorizontalHomeAdapter

    private lateinit var reviseMenu: MenuItem
    private lateinit var okMenu: MenuItem

    private lateinit var recipeList: ArrayList<Recipe>
    private lateinit var userDetailInfo: UserDetail

    private var currentMode: Int? = null
    private var currentOperation: Int? = null

    private var imageUri: Uri? = null
    private var imageUrl: String? = null
    private var currentUserID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.homeToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        storageReference = FirebaseStorage.getInstance().reference

        accountService = RetrofitBuilder.create(this, AccountService::class.java)
        userService = RetrofitBuilder.create(this, UserService::class.java)
        recipeService = RetrofitBuilder.create(this, RecipeService::class.java)

        val userID = DatastoreUtil.getUserBrief(this).userID
        if (intent.getStringExtra("userID") == null || intent.getStringExtra("userID") == userID) {
            currentMode = UIMODE.OWNER
            currentUserID = userID
        } else {
            currentMode = UIMODE.OTHERS
            currentUserID = intent.getStringExtra("userID")
        }

        val factory = PostListViewModelFactory(
            PostRepository(applicationContext)
        )
        postListViewModel = factory.create(PostListViewModel::class.java).apply {
            allPostList.observe(this@HomeActivity, {
                postListAdapter.submitList(it) {}
            })
        }

        binding.apply {
            lifecycleOwner = this@HomeActivity

            postListAdapter = PostListAdapter(
                { },
                { item ->
                    val intent = Intent(this@HomeActivity, PostDetailActivity::class.java)
                        .putExtra("postID", item.postID)
                    startActivity(intent)
                }
            )

            homePostRecycler.apply {
                layoutManager =
                    object : LinearLayoutManager(this@HomeActivity) {
                        override fun canScrollHorizontally(): Boolean {
                            return false
                        }

                        override fun canScrollVertically(): Boolean {
                            return false
                        }
                    }
                adapter = postListAdapter
            }

            postListViewModel.requestPostListById(currentUserID!!)


            recipeHorizontalHomeAdapter = RecipeHorizontalHomeAdapter(currentUserID).apply {
                setOnItemClickListener { _: BaseQuickAdapter<*, *>?, _: View?, position: Int ->
                    val intent = Intent(this@HomeActivity, RecipeDetailActivity::class.java)
                        .putExtra("recipeID", recipeList[position].recipeID)
                    startActivity(intent)
                }
            }

            homeRecipeRecycler.apply {
                layoutManager =
                    LinearLayoutManager(this@HomeActivity, LinearLayoutManager.HORIZONTAL, false)
                adapter = recipeHorizontalHomeAdapter
            }
        }

        if (App.isServerAlive) {
            loadList()
        } else {
            userDetailInfo =
                DataGenerator.make(resources, resources.getInteger(R.integer.DATA_TYPE_USER_DETAIL))
            recipeList =
                DataGenerator.make(resources, resources.getInteger(R.integer.DATE_TYPE_RECIPE))

            setUserInfo()

            recipeHorizontalHomeAdapter.setNewData(recipeList)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (currentMode == UIMODE.OWNER) {
            menuInflater.inflate(R.menu.menu_home_owner, menu)
            reviseMenu = menu.findItem(R.id.menu_home_owner_revise)
            okMenu = menu.findItem(R.id.menu_home_owner_revise_ok)
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onBackPressed() {
        if (currentOperation == OPERATION.REVISE) {
            MaterialDialog(this).show {
                positiveButton(text = "확인") {
                    currentOperation = OPERATION.VIEW
                    reviseMenu.isVisible = true
                    okMenu.isVisible = false
                    binding.apply {
                        homeRevisegroup.visibility = View.GONE
                        ImageLoaderUtil.loadUserImage(
                            this@HomeActivity,
                            userDetailInfo.userImg,
                            homeUserimg
                        )
                        homeNickname.text = userDetailInfo.nickname
                        homeProfiletext.text = userDetailInfo.profileText
                    }
                }
                negativeButton(text = "취소")
            }
        } else super.onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_home_owner_revise -> {
                currentOperation = OPERATION.REVISE
                reviseMenu.isVisible = false
                okMenu.isVisible = true
                binding.homeRevisegroup.visibility = View.VISIBLE
                true
            }
            R.id.menu_home_owner_revise_ok -> {
                updateProfile()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                imageUri = result!!.originalUri
                OtherUtil.log(imageUri.toString())
                GlideApp.with(this)
                    .load(imageUri)
                    .circleCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(binding.homeUserimg)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result!!.error
                error!!.printStackTrace()
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
                App.showToast("권한 거부")
                return
            }
            CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .setCropShape(CropImageView.CropShape.OVAL)
                .setRequestedSize(IMG_WIDTH, IMG_HEIGHT)
                .setMaxCropResultSize(IMG_WIDTH, IMG_HEIGHT)
                .setOutputUri(imageUri)
                .start(this)
        }
    }

    private fun loadList() {
        CoroutineScope(Dispatchers.Main).launch {
            runCatching {
                val res1 = userService.getUserDetail(currentUserID!!)
                userDetailInfo = res1.body()!!

                val res2 = recipeService.getRecipeByUserID(currentUserID!!, "latest")
                val res2Data: List<Recipe?> = res2.body()!!
                    .subList(0, res2.body()!!.size.coerceAtMost(10))
                recipeList = ArrayList(res2Data)

                postListViewModel.requestPostListById(currentUserID!!)

                setUserInfo()

                recipeHorizontalHomeAdapter.apply {
                    setNewData(recipeList)
                    setEmptyView(
                        R.layout.recipe_item_empty, binding.homeRecipeRecycler.parent as ViewGroup
                    )
                }
            }
                .onSuccess { }
                .onFailure {
                    RetrofitBuilder.defaultErrorHandler(it)
                }
        }
    }

    private fun setUserInfo() {
        binding.apply {
            ImageLoaderUtil.loadUserImage(
                this@HomeActivity,
                userDetailInfo.userImg,
                homeUserimg
            )
            homeToolbar.title = userDetailInfo.nickname
            homeNickname.text = userDetailInfo.nickname
            homeProfiletext.text = userDetailInfo.profileText
            homeRecipecount.text = userDetailInfo.recipeCount.toString()
            homeFollowercount.text = userDetailInfo.followerCount.toString()
            homeFollowingcount.text = userDetailInfo.followingCount.toString()
            homeFollowBtn.visibility =
                if (currentMode == UIMODE.OTHERS) View.VISIBLE else View.GONE

            homeUserimgRevise.setOnClickListener { reviseProfileImage() }
            homeNicknameRevise.setOnClickListener { clickReviseNickname() }
            homeProfiletextRevise.setOnClickListener { clickReviseContents() }

            homeRecipewrapper.setOnClickListener {
                val intent = Intent(this@HomeActivity, RecipeMyListActivity::class.java)
                    .putExtra("userID", userDetailInfo.userID)
                startActivity(intent)
            }
            homeFollowerwrapper.setOnClickListener {
                val intent = Intent(this@HomeActivity, FollowListActivity::class.java)
                    .putExtra("MODE", FollowListActivity.UIMODE.FOLLOWER)
                    .putExtra("userID", userDetailInfo.userID)
                startActivity(intent)
            }
            homeFollowingwrapper.setOnClickListener {
                val intent = Intent(this@HomeActivity, FollowListActivity::class.java)
                    .putExtra("MODE", FollowListActivity.UIMODE.FOLLOWING)
                    .putExtra("userID", userDetailInfo.userID)
                startActivity(intent)
            }
        }
    }

    private fun reviseProfileImage() {
        val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

        if (OtherUtil.checkPermission(this, permissions)) {
            CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .setOutputUri(imageUri)
                .setCropShape(CropImageView.CropShape.OVAL)
                .setRequestedSize(IMG_WIDTH, IMG_HEIGHT)
                .setMaxCropResultSize(IMG_WIDTH, IMG_HEIGHT)
                .start(this)
        } else ActivityCompat.requestPermissions(this, permissions, CODE_PERMISSION)
    }

    private fun clickReviseNickname() {
        MaterialDialog(this).show {
            noAutoDismiss()
            title(text = "닉네임 변경")
            input(hint = "닉네임", prefill = binding.homeNickname.text.toString())
            positiveButton(text = "확인", click = { it ->
                when {
                    it.getInputField().text == null ->
                        App.showToast("닉네임을 입력 해 주세요.")
                    it.getInputField().text.length > 12 ->
                        App.showToast("12자 이하 입력 해 주세요.")
                    else -> {
                        CoroutineScope(Dispatchers.Main).launch {
                            runCatching {
                                val res1 =
                                    accountService.checkNickname(it.getInputField().text.toString())

                                val msg = res1.body()!!["msg"].asString
                                OtherUtil.log(msg)
                                if (msg == "ok") {
                                    binding.homeNickname.text = it.getInputField().text
                                    it.dismiss()
                                } else App.showToast("이미 존재합니다.")
                            }
                                .onSuccess { }
                                .onFailure {
                                    RetrofitBuilder.defaultErrorHandler(it)
                                }
                        }
                    }
                }
            })
            negativeButton(text = "취소")
        }
    }

    private fun clickReviseContents() {
        MaterialDialog(this).show {
            noAutoDismiss()
            title(text = "프로필 변경")
            input(
                hint = "닉네임",
                inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE,
                prefill = binding.homeProfiletext.text.toString()
            )
            positiveButton(text = "확인", click = {
                when {
                    it.getInputField().text == null ->
                        App.showToast("프로필을 입력 해 주세요.")
                    it.getInputField().text.length > 60 ->
                        App.showToast("60자 이하 입력 해 주세요.")
                    it.getInputField().lineCount > 4 ->
                        App.showToast("4줄 이하 입력 해 주세요.")
                    else -> {
                        binding.homeProfiletext.text = it.getInputField().text
                        it.dismiss()
                    }
                }
            })
            negativeButton(text = "취소")
        }
    }

    private fun updateProfile() {
        progressON(this)
        if (imageUri != null) {
            imageUrl = String.format(
                getString(R.string.format_upload_file),
                currentUserID, System.currentTimeMillis().toString()
            )
            val ref = storageReference.child(getString(R.string.storage_path_profile) + imageUrl)
            ref.putFile(imageUri!!)
                .addOnSuccessListener { updateToServer() }
        } else {
            imageUrl = userDetailInfo.userImg
            updateToServer()
        }
    }

    private fun updateToServer() {
        CoroutineScope(Dispatchers.Main).launch {
            runCatching {
                accountService.updateUser(
                    userDetailInfo.userID, imageUrl!!,
                    binding.homeNickname.text.toString(),
                    binding.homeProfiletext.text.toString()
                )

                App.showToast("업데이트 되었습니다.")
                currentOperation = OPERATION.VIEW
                reviseMenu.isVisible = true
                okMenu.isVisible = false
                binding.homeRevisegroup.visibility = View.VISIBLE
                imageUri = null
                progressOFF()
            }
                .onSuccess { }
                .onFailure {
                    RetrofitBuilder.defaultErrorHandler(it)
                }
        }
    }
}