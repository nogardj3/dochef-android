package com.yhjoo.dochef.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.gson.JsonObject
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import com.yhjoo.dochef.App
import com.yhjoo.dochef.App.Companion.appInstance
import com.yhjoo.dochef.R
import com.yhjoo.dochef.adapter.PostListAdapter
import com.yhjoo.dochef.adapter.RecipeHorizontalHomeAdapter
import com.yhjoo.dochef.data.DataGenerator
import com.yhjoo.dochef.data.model.Post
import com.yhjoo.dochef.data.model.Recipe
import com.yhjoo.dochef.data.model.UserDetail
import com.yhjoo.dochef.databinding.AHomeBinding
import com.yhjoo.dochef.utils.RxRetrofitServices.*
import com.yhjoo.dochef.model.*
import com.yhjoo.dochef.utils.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.functions.Function
import retrofit2.Response
import java.util.*

class HomeActivity : BaseActivity() {
    private val CODE_PERMISSION = 22
    private val IMG_WIDTH = 360
    private val IMG_HEIGHT = 360

    enum class MODE {
        OWNER, OTHERS
    }

    enum class OPERATION {
        VIEW, REVISE
    }

    var binding: AHomeBinding? = null
    var storageReference: StorageReference? = null
    var accountService: AccountService? = null
    var userService: UserService? = null
    var recipeService: RecipeService? = null
    var postService: PostService? = null
    var recipeHorizontalHomeAdapter: RecipeHorizontalHomeAdapter? = null
    var postListAdapter: PostListAdapter? = null
    var reviseMenu: MenuItem? = null
    var okMenu: MenuItem? = null
    var recipeList: ArrayList<Recipe?>? = null
    var postList: ArrayList<Post?>? = null
    var userDetailInfo: UserDetail? = null
    var currentMode: MODE? = null
    var currentOperation = OPERATION.VIEW
    var mImageUri: Uri? = null
    var image_url: String? = null
    var currentUserID: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AHomeBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        setSupportActionBar(binding!!.homeToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        storageReference = FirebaseStorage.getInstance().reference
        accountService = RxRetrofitBuilder.create(this, AccountService::class.java)
        userService = RxRetrofitBuilder.create(this, UserService::class.java)
        recipeService = RxRetrofitBuilder.create(this, RecipeService::class.java)
        postService = RxRetrofitBuilder.create(this, PostService::class.java)
        val userID = Utils.getUserBrief(this).userID
        if (intent.getStringExtra("userID") == null
            || intent.getStringExtra("userID") == userID
        ) {
            currentMode = MODE.OWNER
            currentUserID = userID
        } else {
            currentMode = MODE.OTHERS
            currentUserID = intent.getStringExtra("userID")
        }
        recipeHorizontalHomeAdapter = RecipeHorizontalHomeAdapter(currentUserID)
        recipeHorizontalHomeAdapter!!.setOnItemClickListener { adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int ->
            val intent = Intent(this@HomeActivity, RecipeDetailActivity::class.java)
                .putExtra("recipeID", recipeList!![position].getRecipeID())
            startActivity(intent)
        }
        binding!!.homeRecipeRecycler.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding!!.homeRecipeRecycler.adapter = recipeHorizontalHomeAdapter
        postListAdapter = PostListAdapter()
        postListAdapter!!.setOnItemClickListener { adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int ->
            val intent = Intent(this@HomeActivity, PostDetailActivity::class.java)
                .putExtra("postID", postList!![position].getPostID())
            startActivity(intent)
        }
        binding!!.homePostRecycler.layoutManager = object : LinearLayoutManager(this) {
            override fun canScrollHorizontally(): Boolean {
                return false
            }

            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        binding!!.homePostRecycler.adapter = postListAdapter
        if (App.isServerAlive()) {
            loadList()
        } else {
            userDetailInfo =
                DataGenerator.make(resources, resources.getInteger(R.integer.DATA_TYPE_USER_DETAIL))
            recipeList = DataGenerator.make(
                resources, resources.getInteger(R.integer.DATE_TYPE_RECIPE)
            )
            postList = DataGenerator.make(resources, resources.getInteger(R.integer.DATA_TYPE_POST))
            setUserInfo()
            recipeHorizontalHomeAdapter!!.setNewData(recipeList)
            postListAdapter!!.setNewData(postList)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (currentMode == MODE.OWNER) {
            menuInflater.inflate(R.menu.menu_home_owner, menu)
            reviseMenu = menu.findItem(R.id.menu_home_owner_revise)
            okMenu = menu.findItem(R.id.menu_home_owner_revise_ok)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onBackPressed() {
        if (currentOperation == OPERATION.REVISE) {
            BaseActivity.Companion.createConfirmDialog(this,
                null, "변경이 취소됩니다.",
                SingleButtonCallback { dialog1: MaterialDialog?, which: DialogAction? ->
                    currentOperation = OPERATION.VIEW
                    reviseMenu!!.isVisible = true
                    okMenu!!.isVisible = false
                    binding!!.homeRevisegroup.visibility = View.GONE
                    ImageLoadUtil.loadUserImage(
                        this,
                        userDetailInfo.getUserImg(),
                        binding!!.homeUserimg
                    )
                    binding!!.homeNickname.text = userDetailInfo.getNickname()
                    binding!!.homeProfiletext.text = userDetailInfo.getProfileText()
                }
            )
                .show()
        } else super.onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_home_owner_revise) {
            currentOperation = OPERATION.REVISE
            reviseMenu!!.isVisible = false
            okMenu!!.isVisible = true
            binding!!.homeRevisegroup.visibility = View.VISIBLE
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
                mImageUri = result.uri
                Utils.log(result.uri)
                GlideApp.with(this)
                    .load(mImageUri)
                    .circleCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(binding!!.homeUserimg)
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
                .setCropShape(CropImageView.CropShape.OVAL)
                .setRequestedSize(IMG_WIDTH, IMG_HEIGHT)
                .setMaxCropResultSize(IMG_WIDTH, IMG_HEIGHT)
                .setOutputUri(mImageUri)
                .start(this)
        }
    }

    fun loadList() {
        compositeDisposable!!.add(
            userService!!.getUserDetail(currentUserID)
                .flatMap(Function { response: Response<UserDetail?>? ->
                    userDetailInfo = response!!.body()
                    recipeService!!.getRecipeByUserID(currentUserID, "latest")
                } as Function<Response<UserDetail?>?, Single<Response<ArrayList<Recipe?>?>?>?>)
                .flatMap(Function { response: Response<ArrayList<Recipe?>?>? ->
                    val res: List<Recipe?> = response!!.body()!!
                        .subList(0, Math.min(response.body()!!.size, 10))
                    recipeList = ArrayList(res)
                    postService!!.getPostListByUserID(currentUserID)
                        .observeOn(AndroidSchedulers.mainThread())
                } as Function<Response<ArrayList<Recipe?>?>?, Single<Response<ArrayList<Post?>?>?>>)
                .subscribe({ response: Response<ArrayList<Post?>?>? ->
                    postList = response!!.body()
                    setUserInfo()
                    recipeHorizontalHomeAdapter!!.setNewData(recipeList)
                    recipeHorizontalHomeAdapter!!.setEmptyView(
                        R.layout.rv_empty_recipe, binding!!.homeRecipeRecycler.parent as ViewGroup
                    )
                    postListAdapter!!.setNewData(postList)
                    postListAdapter!!.setEmptyView(
                        R.layout.rv_empty_post, binding!!.homePostRecycler.parent as ViewGroup
                    )
                }, RxRetrofitBuilder.defaultConsumer())
        )
    }

    fun setUserInfo() {
        ImageLoadUtil.loadUserImage(this, userDetailInfo.getUserImg(), binding!!.homeUserimg)
        binding!!.homeToolbar.title = userDetailInfo.getNickname()
        binding!!.homeNickname.text = userDetailInfo.getNickname()
        binding!!.homeProfiletext.text = userDetailInfo.getProfileText()
        binding!!.homeRecipecount.text = Integer.toString(userDetailInfo.getRecipeCount())
        binding!!.homeFollowercount.text = Integer.toString(userDetailInfo.getFollowerCount())
        binding!!.homeFollowingcount.text = Integer.toString(userDetailInfo.getFollowingCount())
        binding!!.homeFollowBtn.visibility =
            if (currentMode == MODE.OTHERS) View.VISIBLE else View.GONE
        binding!!.homeUserimgRevise.setOnClickListener { v: View? -> reviseProfileImage(v) }
        binding!!.homeNicknameRevise.setOnClickListener { v: View? -> clickReviseNickname(v) }
        binding!!.homeProfiletextRevise.setOnClickListener { v: View? -> clickReviseContents(v) }
        binding!!.homeRecipewrapper.setOnClickListener { v: View? ->
            val intent = Intent(this@HomeActivity, RecipeMyListActivity::class.java)
                .putExtra("userID", userDetailInfo.getUserID())
            startActivity(intent)
        }
        binding!!.homeFollowerwrapper.setOnClickListener { v: View? ->
            val intent = Intent(this@HomeActivity, FollowListActivity::class.java)
                .putExtra("MODE", FollowListActivity.MODE.FOLLOWER)
                .putExtra("userID", userDetailInfo.getUserID())
            startActivity(intent)
        }
        binding!!.homeFollowingwrapper.setOnClickListener { v: View? ->
            val intent = Intent(this@HomeActivity, FollowListActivity::class.java)
                .putExtra("MODE", FollowListActivity.MODE.FOLLOWING)
                .putExtra("userID", userDetailInfo.getUserID())
            startActivity(intent)
        }
    }

    fun reviseProfileImage(v: View?) {
        val permissions = arrayOf<String?>(
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        if (Utils.checkPermission(this, permissions)) {
            CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .setOutputUri(mImageUri)
                .setCropShape(CropImageView.CropShape.OVAL)
                .setRequestedSize(IMG_WIDTH, IMG_HEIGHT)
                .setMaxCropResultSize(IMG_WIDTH, IMG_HEIGHT)
                .start(this)
        } else ActivityCompat.requestPermissions(this, permissions, CODE_PERMISSION)
    }

    fun clickReviseNickname(v: View?) {
        val materialDialog = MaterialDialog.Builder(this)
            .autoDismiss(false)
            .title("닉네임 변경")
            .inputType(InputType.TYPE_CLASS_TEXT)
            .inputRange(4, 12)
            .input(
                "닉네임",
                binding!!.homeNickname.text.toString()
            ) { dialog: MaterialDialog?, input: CharSequence? -> }
            .positiveText("확인")
            .positiveColorRes(R.color.grey_text)
            .onPositive { dialog: MaterialDialog, which: DialogAction? ->
                if (dialog.inputEditText!!
                        .text == null
                ) {
                    appInstance!!.showToast("닉네임을 입력 해 주세요.")
                } else if (dialog.inputEditText!!.text.toString().length > 12) {
                    appInstance!!.showToast("12자 이하 입력 해 주세요.")
                } else {
                    compositeDisposable!!.add(
                        accountService!!.checkNickname(dialog.inputEditText!!.text.toString())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ response: Response<JsonObject?>? ->
                                val msg = response!!.body()!!["msg"].asString
                                Utils.log(msg)
                                if (msg == "ok") {
                                    binding!!.homeNickname.text =
                                        dialog.inputEditText!!.text.toString()
                                    dialog.dismiss()
                                } else appInstance!!.showToast("이미 존재합니다.")
                            }, RxRetrofitBuilder.defaultConsumer())
                    )
                }
            }
            .negativeText("취소")
            .negativeColorRes(R.color.grey_text)
            .onNegative { dialog: MaterialDialog, which: DialogAction? -> dialog.dismiss() }
            .build()
        materialDialog.show()
    }

    fun clickReviseContents(v: View?) {
        val materialDialog = MaterialDialog.Builder(this)
            .autoDismiss(false)
            .title("프로필 변경")
            .inputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE)
            .inputRange(0, 60)
            .input(
                "프로필",
                binding!!.homeProfiletext.text.toString()
            ) { dialog: MaterialDialog?, input: CharSequence? -> }
            .positiveText("확인")
            .positiveColorRes(R.color.grey_text)
            .onPositive { dialog: MaterialDialog, which: DialogAction? ->
                if (dialog.inputEditText!!
                        .text == null
                ) {
                    appInstance!!.showToast("프로필을 입력 해 주세요.")
                } else if (dialog.inputEditText!!.text.toString().length > 60) {
                    appInstance!!.showToast("60자 이하 입력 해 주세요.")
                } else if (dialog.inputEditText!!.lineCount > 4) {
                    appInstance!!.showToast("4줄 이하 입력 해 주세요.")
                } else {
                    binding!!.homeProfiletext.text = dialog.inputEditText!!.text.toString()
                    dialog.dismiss()
                }
            }
            .negativeText("취소")
            .negativeColorRes(R.color.grey_text)
            .onNegative { dialog: MaterialDialog, which: DialogAction? -> dialog.dismiss() }
            .build()
        materialDialog.show()
    }

    fun updateProfile() {
        progressON(this)
        image_url = ""
        if (mImageUri != null) {
            image_url = String.format(
                getString(R.string.format_upload_file),
                currentUserID, java.lang.Long.toString(System.currentTimeMillis())
            )
            val ref = storageReference!!.child(getString(R.string.storage_path_profile) + image_url)
            ref.putFile(mImageUri!!)
                .addOnSuccessListener { taskSnapshot: UploadTask.TaskSnapshot? -> updateToServer() }
        } else {
            image_url = userDetailInfo.getUserImg()
            updateToServer()
        }
    }

    fun updateToServer() {
        compositeDisposable!!.add(
            accountService!!.updateUser(
                userDetailInfo.getUserID(), image_url,
                binding!!.homeNickname.text.toString(),
                binding!!.homeProfiletext.text.toString()
            )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response: Response<JsonObject?>? ->
                    appInstance!!.showToast("업데이트 되었습니다.")
                    currentOperation = OPERATION.VIEW
                    reviseMenu!!.isVisible = true
                    okMenu!!.isVisible = false
                    binding!!.homeRevisegroup.visibility = View.VISIBLE
                    mImageUri = null
                    progressOFF()
                }, RxRetrofitBuilder.defaultConsumer())
        )
    }
}