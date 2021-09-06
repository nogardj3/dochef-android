package com.yhjoo.dochef.ui.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.getInputField
import com.afollestad.materialdialogs.input.input
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.JsonObject
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import com.yhjoo.dochef.App.Companion.appInstance
import com.yhjoo.dochef.R
import com.yhjoo.dochef.activities.PostDetailActivity
import com.yhjoo.dochef.activities.RecipeDetailActivity
import com.yhjoo.dochef.activities.RecipeMyListActivity
import com.yhjoo.dochef.adapter.PostListAdapter
import com.yhjoo.dochef.adapter.RecipeHorizontalHomeAdapter
import com.yhjoo.dochef.data.DataGenerator
import com.yhjoo.dochef.data.model.Post
import com.yhjoo.dochef.data.model.Recipe
import com.yhjoo.dochef.data.model.UserDetail
import com.yhjoo.dochef.databinding.AHomeBinding
import com.yhjoo.dochef.utils.*
import com.yhjoo.dochef.utils.RxRetrofitServices.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import retrofit2.Response
import java.util.*

class HomeActivity : BaseActivity() {
    companion object{
        const val CODE_PERMISSION = 22
        const val IMG_WIDTH = 360
        const val IMG_HEIGHT = 360
    }

    object UIMODE{
        const val OWNER = 0
        const val OTHERS = 1
    }

    object OPERATION{
        const val VIEW = 0
        const val REVISE = 1
    }

    val binding: AHomeBinding by lazy {AHomeBinding.inflate(layoutInflater)}

    private lateinit var storageReference: StorageReference
    private lateinit var accountService: AccountService
    private lateinit var userService: UserService
    private lateinit var recipeService: RecipeService
    private lateinit var postService: PostService

    private lateinit var recipeHorizontalHomeAdapter: RecipeHorizontalHomeAdapter
    private lateinit var postListAdapter: PostListAdapter

    private lateinit var reviseMenu: MenuItem
    private lateinit var okMenu: MenuItem

    private lateinit var recipeList: ArrayList<Recipe>
    private lateinit var postList: ArrayList<Post>
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
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        storageReference = FirebaseStorage.getInstance().reference

        accountService = RxRetrofitBuilder.create(this, AccountService::class.java)
        userService = RxRetrofitBuilder.create(this, UserService::class.java)
        recipeService = RxRetrofitBuilder.create(this, RecipeService::class.java)
        postService = RxRetrofitBuilder.create(this, PostService::class.java)

        val userID = Utils.getUserBrief(this).userID
        if (intent.getStringExtra("userID") == null|| intent.getStringExtra("userID") == userID) {
            currentMode = UIMODE.OWNER
            currentUserID = userID
        } else {
            currentMode = UIMODE.OTHERS
            currentUserID = intent.getStringExtra("userID")
        }

        recipeHorizontalHomeAdapter = RecipeHorizontalHomeAdapter(currentUserID)
        recipeHorizontalHomeAdapter.setOnItemClickListener { _: BaseQuickAdapter<*, *>?, _: View?, position: Int ->
            val intent = Intent(this@HomeActivity, RecipeDetailActivity::class.java)
                .putExtra("recipeID", recipeList[position].recipeID)
            startActivity(intent)
        }

        binding.apply {
            homeRecipeRecycler.layoutManager =
                LinearLayoutManager(this@HomeActivity, LinearLayoutManager.HORIZONTAL, false)
            homeRecipeRecycler.adapter = recipeHorizontalHomeAdapter

            postListAdapter = PostListAdapter()
            postListAdapter.setOnItemClickListener { _: BaseQuickAdapter<*, *>?, _: View?, position: Int ->
                val intent = Intent(this@HomeActivity, PostDetailActivity::class.java)
                    .putExtra("postID", postList[position].postID)
                startActivity(intent)
            }

            binding.homePostRecycler.layoutManager = object : LinearLayoutManager(this@HomeActivity) {
                override fun canScrollHorizontally(): Boolean {
                    return false
                }

                override fun canScrollVertically(): Boolean {
                    return false
                }
            }
            binding.homePostRecycler.adapter = postListAdapter
        }

        if (appInstance.isServerAlive) {
            loadList()
        } else {
            userDetailInfo = DataGenerator.make(resources, resources.getInteger(R.integer.DATA_TYPE_USER_DETAIL))
            recipeList = DataGenerator.make(resources, resources.getInteger(R.integer.DATE_TYPE_RECIPE))
            postList = DataGenerator.make(resources, resources.getInteger(R.integer.DATA_TYPE_POST))

            setUserInfo()

            recipeHorizontalHomeAdapter.setNewData(recipeList)
            postListAdapter.setNewData(postList)
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
            MaterialDialog(this).show{
                positiveButton(text="확인"){
                    currentOperation = OPERATION.VIEW
                    reviseMenu.isVisible = true
                    okMenu.isVisible = false
                    binding.apply {
                        homeRevisegroup.visibility = View.GONE
                        ImageLoadUtil.loadUserImage(
                            this@HomeActivity,
                            userDetailInfo.userImg,
                            homeUserimg
                        )
                        homeNickname.text = userDetailInfo.nickname
                        homeProfiletext.text = userDetailInfo.profileText
                    }
                }
                negativeButton(text="취소")
            }
        } else super.onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_home_owner_revise) {
            currentOperation = OPERATION.REVISE
            reviseMenu.isVisible = false
            okMenu.isVisible = true
            binding.homeRevisegroup.visibility = View.VISIBLE
        } else if (item.itemId == R.id.menu_home_owner_revise_ok) {
            updateProfile()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                imageUri = result.uri
                Utils.log(imageUri.toString())
                GlideApp.with(this)
                    .load(imageUri)
                    .circleCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(binding.homeUserimg)
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
                appInstance.showToast("권한 거부")
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
        compositeDisposable.add(
            userService.getUserDetail(currentUserID!!)
                .flatMap {response: Response<UserDetail> ->
                    userDetailInfo = response.body()!!
                    recipeService.getRecipeByUserID(currentUserID!!, "latest")
                }
                .flatMap{response: Response<ArrayList<Recipe>> ->
                    val res: List<Recipe?> = response.body()!!
                        .subList(0, response.body()!!.size.coerceAtMost(10))
                    recipeList = ArrayList(res)
                    postService.getPostListByUserID(currentUserID!!)
                        .observeOn(AndroidSchedulers.mainThread())
                }
                .subscribe({ response: Response<ArrayList<Post>> ->
                    postList = response.body()!!
                    setUserInfo()

                    recipeHorizontalHomeAdapter.setNewData(recipeList)
                    recipeHorizontalHomeAdapter.setEmptyView(
                        R.layout.rv_empty_recipe, binding.homeRecipeRecycler.parent as ViewGroup
                    )
                    postListAdapter.setNewData(postList)
                    postListAdapter.setEmptyView(
                        R.layout.rv_empty_post, binding.homePostRecycler.parent as ViewGroup
                    )
                }, RxRetrofitBuilder.defaultConsumer())
        )
    }

    private fun setUserInfo() {
        ImageLoadUtil.loadUserImage(this, userDetailInfo.userImg, binding.homeUserimg)

        binding.homeToolbar.title = userDetailInfo.nickname
        binding.homeNickname.text = userDetailInfo.nickname
        binding.homeProfiletext.text = userDetailInfo.profileText
        binding.homeRecipecount.text = userDetailInfo.recipeCount.toString()
        binding.homeFollowercount.text = userDetailInfo.followerCount.toString()
        binding.homeFollowingcount.text = userDetailInfo.followingCount.toString()
        binding.homeFollowBtn.visibility =
            if (currentMode == UIMODE.OTHERS) View.VISIBLE else View.GONE

        binding.homeUserimgRevise.setOnClickListener { reviseProfileImage() }
        binding.homeNicknameRevise.setOnClickListener { clickReviseNickname() }
        binding.homeProfiletextRevise.setOnClickListener { clickReviseContents() }

        binding.homeRecipewrapper.setOnClickListener {
            val intent = Intent(this@HomeActivity, RecipeMyListActivity::class.java)
                .putExtra("userID", userDetailInfo.userID)
            startActivity(intent)
        }
        binding.homeFollowerwrapper.setOnClickListener {
            val intent = Intent(this@HomeActivity, FollowListActivity::class.java)
                .putExtra("MODE", FollowListActivity.UIMODE.FOLLOWER)
                .putExtra("userID", userDetailInfo.userID)
            startActivity(intent)
        }
        binding.homeFollowingwrapper.setOnClickListener {
            val intent = Intent(this@HomeActivity, FollowListActivity::class.java)
                .putExtra("MODE", FollowListActivity.UIMODE.FOLLOWING)
                .putExtra("userID", userDetailInfo.userID)
            startActivity(intent)
        }
    }

    private fun reviseProfileImage() {
        val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

        if (Utils.checkPermission(this, permissions)) {
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
            title(text="닉네임 변경")
            input(hint="닉네임",prefill = binding!!.homeNickname.text.toString())
            positiveButton(text="확인",click = {
                when {
                    it.getInputField().text == null ->
                        appInstance.showToast("닉네임을 입력 해 주세요.")
                    it.getInputField().text.length > 12 ->
                        appInstance.showToast("12자 이하 입력 해 주세요.")
                    else ->{
                        compositeDisposable.add(
                            accountService.checkNickname(it.getInputField().text.toString())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({ response: Response<JsonObject> ->
                                    val msg = response.body()!!["msg"].asString
                                    Utils.log(msg)
                                    if (msg == "ok") {
                                        binding.homeNickname.text = it.getInputField().text
                                        it.dismiss()
                                    } else appInstance.showToast("이미 존재합니다.")
                                }, RxRetrofitBuilder.defaultConsumer())
                        )
                    }
                }
            })
            negativeButton(text="취소")
        }
    }

    private fun clickReviseContents() {
        MaterialDialog(this).show {
            noAutoDismiss()
            title(text="프로필 변경")
            input(hint="닉네임",inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE,
                prefill = binding!!.homeProfiletext.text.toString())
            positiveButton(text="확인",click = {
                when {
                    it.getInputField().text == null ->
                        appInstance.showToast("프로필을 입력 해 주세요.")
                    it.getInputField().text.length > 60 ->
                        appInstance.showToast("60자 이하 입력 해 주세요.")
                    it.getInputField().lineCount > 4 ->
                        appInstance.showToast("4줄 이하 입력 해 주세요.")
                    else ->{
                        binding.homeProfiletext.text = it.getInputField().text
                        it.dismiss()
                    }
                }
            })
            negativeButton(text="취소")
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
        compositeDisposable.add(
            accountService.updateUser(
                userDetailInfo.userID, imageUrl!!,
                binding.homeNickname.text.toString(),
                binding.homeProfiletext.text.toString()
            )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    appInstance.showToast("업데이트 되었습니다.")
                    currentOperation = OPERATION.VIEW
                    reviseMenu.isVisible = true
                    okMenu.isVisible = false
                    binding.homeRevisegroup.visibility = View.VISIBLE
                    imageUri = null
                    progressOFF()
                }, RxRetrofitBuilder.defaultConsumer())
        )
    }
}