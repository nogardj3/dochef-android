package com.yhjoo.dochef.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.*
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.getInputField
import com.afollestad.materialdialogs.input.input
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.yhjoo.dochef.*
import com.yhjoo.dochef.data.model.Post
import com.yhjoo.dochef.data.model.Recipe
import com.yhjoo.dochef.data.model.UserDetail
import com.yhjoo.dochef.data.network.RetrofitServices.*
import com.yhjoo.dochef.data.repository.*
import com.yhjoo.dochef.databinding.HomeActivityBinding
import com.yhjoo.dochef.ui.base.BaseActivity
import com.yhjoo.dochef.ui.follow.FollowListActivity
import com.yhjoo.dochef.ui.post.PostDetailActivity
import com.yhjoo.dochef.ui.recipe.RecipeDetailActivity
import com.yhjoo.dochef.ui.recipe.RecipeMyListActivity
import com.yhjoo.dochef.utils.*
import java.util.*

class HomeActivity : BaseActivity() {
    private val binding: HomeActivityBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.home_activity)
    }
    private val homeViewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(
            application,
            intent,
            UserRepository(applicationContext),
            RecipeRepository(applicationContext),
            PostRepository(applicationContext),
            AccountRepository(applicationContext),
        )
    }
    private lateinit var recipeListAdapter: RecipeListAdapter
    private lateinit var postListAdapter: PostListAdapter

    private lateinit var reviseMenu: MenuItem
    private lateinit var okMenu: MenuItem

    private lateinit var originUserInfo: UserDetail
    private lateinit var nicknameDialog: MaterialDialog
    private var imageUri: Uri? = null
    private val cropimage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            imageUri = result.uriContent
            OtherUtil.log(imageUri.toString())
            GlideApp.with(this)
                .load(imageUri)
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(binding.homeUserimg)
        } else {
            val error = result.error
            error!!.printStackTrace()
        }
    }

    private var currentMode: Int? = null
    private var currentOperation: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.homeToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        currentMode =
            if (intent.getStringExtra("userID") == null
                || intent.getStringExtra("userID") == homeViewModel.activeUserId
            )
                UIMODE.OWNER
            else UIMODE.OTHERS

        binding.apply {
            lifecycleOwner = this@HomeActivity

            recipeListAdapter = RecipeListAdapter(homeViewModel.activeUserId)
            { goRecipeDetail(it) }

            postListAdapter = PostListAdapter(
                { goHome(it) },
                { goPostDetail(it) }
            )

            homeRecipeRecycler.apply {
                layoutManager =
                    LinearLayoutManager(this@HomeActivity, LinearLayoutManager.HORIZONTAL, false)
                adapter = recipeListAdapter
            }

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

            homeViewModel.userDetail.observe(this@HomeActivity, {
                originUserInfo = it
                setUserInfo(it)
            })

            homeViewModel.allRecipes.observe(this@HomeActivity, {
                homeRecipeEmpty.isVisible = it.isEmpty()
                recipeListAdapter.submitList(it)
            })

            homeViewModel.allPosts.observe(this@HomeActivity, {
                homePostEmpty.isVisible = it.isEmpty()
                postListAdapter.submitList(it)
            })

            homeViewModel.updateComplete.observe(this@HomeActivity, { result ->
                if (result) updateComplete()
            })

            homeViewModel.nicknameValid.observe(this@HomeActivity, { result ->
                if (result.first) {
                    App.showToast("사용 가능한 아이디입니다.")
                    binding.homeNickname.text = result.second
                    nicknameDialog.dismiss()
                } else {
                    App.showToast("이미 존재합니다.")
                }
            })
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
                message(text = "변경을 취소하시겠습니까?")
                positiveButton(text = "확인") {
                    currentOperation = OPERATION.VIEW
                    reviseMenu.isVisible = true
                    okMenu.isVisible = false
                    binding.apply {
                        homeRevisegroup.isVisible = false
                        ImageLoaderUtil.loadUserImage(
                            this@HomeActivity,
                            originUserInfo.userImg,
                            homeUserimg
                        )
                        homeNickname.text = originUserInfo.nickname
                        homeProfiletext.text = originUserInfo.profileText
                    }
                }
                negativeButton(text = "취소", click = {
                    it.dismiss()
                })
            }
        } else super.onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_home_owner_revise -> {
                currentOperation = OPERATION.REVISE
                reviseMenu.isVisible = false
                okMenu.isVisible = true
                binding.homeRevisegroup.isVisible = true
                true
            }
            R.id.menu_home_owner_revise_ok -> {
                updateProfile()
                true
            }
            else -> super.onOptionsItemSelected(item)
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
                showSnackBar(binding.root, "권한 거부")
                return
            }

            launchCrop()
        }
    }

    private fun setUserInfo(userDetail: UserDetail) {
        binding.apply {
            ImageLoaderUtil.loadUserImage(
                this@HomeActivity,
                userDetail.userImg,
                homeUserimg
            )
            homeToolbar.title = userDetail.nickname
            homeNickname.text = userDetail.nickname
            homeProfiletext.text = userDetail.profileText
            homeRecipecount.text = userDetail.recipeCount.toString()
            homeFollowercount.text = userDetail.followerCount.toString()
            homeFollowingcount.text = userDetail.followingCount.toString()
            homeFollowBtn.isVisible = currentMode == UIMODE.OTHERS

            homeUserimgRevise.setOnClickListener { reviseProfileImage() }
            homeNicknameRevise.setOnClickListener { clickReviseNickname() }
            homeProfiletextRevise.setOnClickListener { clickReviseContents() }

            homeRecipewrapper.setOnClickListener { goMyRecipe(userDetail) }
            homeFollowerwrapper.setOnClickListener { goFollowerList(userDetail) }
            homeFollowingwrapper.setOnClickListener { goFollowingList(userDetail) }
        }
    }

    private fun goRecipeDetail(item: Recipe) {
        startActivity(
            Intent(this@HomeActivity, RecipeDetailActivity::class.java)
                .putExtra("recipeID", item.recipeID)
        )
    }

    private fun goHome(item: Post) {
//  TODO
//  다른 사람 HomeActivity로 가기, 클릭 겹침
//        Intent(this@HomeActivity, HomeActivity::class.java)
//            .putExtra("postID", item.postID).apply {
//                startActivity(intent)
//            }
    }

    private fun goPostDetail(item: Post) {
        startActivity(
            Intent(this@HomeActivity, PostDetailActivity::class.java)
                .putExtra("postID", item.postID)
        )
    }


    private fun goMyRecipe(item: UserDetail) {
        startActivity(
            Intent(this@HomeActivity, RecipeMyListActivity::class.java)
                .putExtra("userID", item.userID)
        )
    }

    private fun goFollowerList(item: UserDetail) {
        startActivity(
            Intent(this@HomeActivity, FollowListActivity::class.java)
                .putExtra("MODE", FollowListActivity.FOLLOWER)
                .putExtra("userID", item.userID)
        )
    }

    private fun goFollowingList(item: UserDetail) {
        startActivity(
            Intent(this@HomeActivity, FollowListActivity::class.java)
                .putExtra("MODE", FollowListActivity.FOLLOWING)
                .putExtra("userID", item.userID)
        )
    }


    private fun reviseProfileImage() {
        val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

        if (OtherUtil.checkPermission(this, permissions)) launchCrop()
        else {
            ActivityCompat.requestPermissions(
                this,
                permissions,
                Constants.PERMISSION_CODE
            )
        }
    }

    private fun launchCrop() {
        cropimage.launch(
            options {
                setGuidelines(CropImageView.Guidelines.ON)
                setAspectRatio(1, 1)
                setOutputUri(imageUri)
                setCropShape(CropImageView.CropShape.OVAL)
                setRequestedSize(
                    Constants.IMAGE.SIZE.PROFILE.IMG_WIDTH,
                    Constants.IMAGE.SIZE.PROFILE.IMG_HEIGHT
                )
                setMaxCropResultSize(
                    Constants.IMAGE.SIZE.PROFILE.IMG_WIDTH,
                    Constants.IMAGE.SIZE.PROFILE.IMG_HEIGHT
                )
            }
        )
    }

    private fun clickReviseNickname() {
        nicknameDialog = MaterialDialog(this)
            .noAutoDismiss()
            .title(text = "닉네임 변경")
            .input(hint = "닉네임", prefill = binding.homeNickname.text.toString())
            .positiveButton(text = "확인", click = {
                when {
                    it.getInputField().text == null ->
                        showSnackBar(binding.root, "닉네임을 입력 해 주세요.")
                    it.getInputField().text.length > 12 ->
                        showSnackBar(binding.root, "12자 이하 입력 해 주세요.")
                    else -> {
                        hideKeyboard(it.view)
                        homeViewModel.checkNickname(it.getInputField().text.toString())
                    }

                }
            })
            .negativeButton(text = "취소", click = {
                it.dismiss()
            })
        nicknameDialog.show()
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
                        showSnackBar(binding.root, "프로필을 입력 해 주세요.")
                    it.getInputField().text.length > 60 ->
                        showSnackBar(binding.root, "60자 이하 입력 해 주세요.")
                    it.getInputField().lineCount > 4 ->
                        showSnackBar(binding.root, "4줄 이하 입력 해 주세요.")
                    else -> {
                        binding.homeProfiletext.text = it.getInputField().text
                        it.dismiss()
                    }
                }
            })
            negativeButton(text = "취소", click = {
                it.dismiss()
            })
        }
    }

    private fun updateProfile() {
        showProgress(this)

        homeViewModel.updateProfile(
            imageUri,
            binding.homeNickname.text.toString(),
            binding.homeProfiletext.text.toString()
        )
    }

    private fun updateComplete() {
        showSnackBar(binding.root, "업데이트 되었습니다.")
        currentOperation = OPERATION.VIEW
        reviseMenu.isVisible = true
        okMenu.isVisible = false
        binding.homeRevisegroup.isVisible = false
        imageUri = null
        hideProgress()

        homeViewModel.requestActiveUserDetail()
    }

    object UIMODE {
        const val OWNER = 0
        const val OTHERS = 1
    }

    object OPERATION {
        const val VIEW = 0
        const val REVISE = 1
    }
}