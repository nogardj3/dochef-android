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
import com.bumptech.glide.signature.ObjectKey
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.yhjoo.dochef.*
import com.yhjoo.dochef.data.RetrofitServices.*
import com.yhjoo.dochef.data.model.Post
import com.yhjoo.dochef.data.model.Recipe
import com.yhjoo.dochef.data.model.UserDetail
import com.yhjoo.dochef.data.repository.*
import com.yhjoo.dochef.databinding.HomeActivityBinding
import com.yhjoo.dochef.ui.base.BaseActivity
import com.yhjoo.dochef.ui.follow.FollowListActivity
import com.yhjoo.dochef.ui.post.PostDetailActivity
import com.yhjoo.dochef.ui.recipe.RecipeDetailActivity
import com.yhjoo.dochef.ui.recipe.RecipeMyListActivity
import com.yhjoo.dochef.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.util.*

@AndroidEntryPoint
class HomeActivity : BaseActivity() {
    // TODO
    // menu databinding
    // permission, onactivity databinding

    private val binding: HomeActivityBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.home_activity)
    }
    private val homeViewModel: HomeViewModel by viewModels ()

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
                .signature(ObjectKey(System.currentTimeMillis().toString()))
                .into(binding.homeUserimg)
        } else {
            val error = result.error
            error!!.printStackTrace()
        }
    }

    private var currentOperation: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.homeToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.apply {
            lifecycleOwner = this@HomeActivity
            activity = this@HomeActivity
            viewModel = homeViewModel

            recipeListAdapter = RecipeListAdapter(this@HomeActivity)
            homeRecipeRecycler.adapter = recipeListAdapter

            postListAdapter = PostListAdapter(this@HomeActivity)

            homePostRecycler.also {
                it.layoutManager =
                    object : LinearLayoutManager(this@HomeActivity) {
                        override fun canScrollHorizontally(): Boolean {
                            return false
                        }

                        override fun canScrollVertically(): Boolean {
                            return false
                        }
                    }
                it.adapter = postListAdapter
            }
        }

        homeViewModel.userDetail.observe(this@HomeActivity, {
            originUserInfo = it
        })

        homeViewModel.allRecipes.observe(this@HomeActivity, {
            binding.homeRecipeEmpty.isVisible = it.isEmpty()
            recipeListAdapter.submitList(it)
        })

        homeViewModel.allPosts.observe(this@HomeActivity, {
            binding.homePostEmpty.isVisible = it.isEmpty()
            postListAdapter.submitList(it)
        })

        subscribeEventOnLifecycle {
            homeViewModel.eventResult.collect {
                when (it.first) {
                    HomeViewModel.Events.UPDATE_COMPLETE -> {
                        showSnackBar(binding.root, "???????????? ???????????????.")
                        currentOperation = OPERATION.VIEW
                        reviseMenu.isVisible = true
                        okMenu.isVisible = false
                        binding.homeRevisegroup.isVisible = false
                        imageUri = null
                        hideProgress()
                    }
                    HomeViewModel.Events.NICKNAME_VALID -> {
                        App.showToast("?????? ????????? ??????????????????.")
                        binding.homeNickname.text = it.second
                        nicknameDialog.dismiss()
                    }
                    HomeViewModel.Events.NICKNAME_INVALID -> {
                        App.showToast("?????? ???????????????.")
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (homeViewModel.currentMode == UIMODE.OWNER) {
            menuInflater.inflate(R.menu.menu_home_owner, menu)
            reviseMenu = menu.findItem(R.id.menu_home_owner_revise)
            okMenu = menu.findItem(R.id.menu_home_owner_revise_ok)
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onBackPressed() {
        if (currentOperation == OPERATION.REVISE) {
            MaterialDialog(this).show {
                message(text = "????????? ?????????????????????????")
                positiveButton(text = "??????") {
                    currentOperation = OPERATION.VIEW
                    reviseMenu.isVisible = true
                    okMenu.isVisible = false
                    binding.apply {
                        homeRevisegroup.isVisible = false
                        BindUtil.loadUserImage(originUserInfo.userImg, homeUserimg)
                        homeNickname.text = originUserInfo.nickname
                        homeProfiletext.text = originUserInfo.profileText
                    }
                }
                negativeButton(text = "??????", click = {
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
                showProgress(this)

                homeViewModel.updateProfile(
                    imageUri,
                    binding.homeNickname.text.toString(),
                    binding.homeProfiletext.text.toString()
                )
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
                showSnackBar(binding.root, "?????? ??????")
                return
            }

            launchCrop()
        }
    }

    fun reviseProfileImage() {
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

    fun clickReviseNickname() {
        nicknameDialog = MaterialDialog(this)
            .noAutoDismiss()
            .title(text = "????????? ??????")
            .input(hint = "?????????", prefill = binding.homeNickname.text.toString())
            .positiveButton(text = "??????", click = {
                when {
                    it.getInputField().text == null ->
                        showSnackBar(binding.root, "???????????? ?????? ??? ?????????.")
                    it.getInputField().text.length > 12 ->
                        showSnackBar(binding.root, "12??? ?????? ?????? ??? ?????????.")
                    else -> {
                        hideKeyboard(it.view)
                        homeViewModel.checkNickname(it.getInputField().text.toString())
                    }

                }
            })
            .negativeButton(text = "??????", click = {
                it.dismiss()
            })
        nicknameDialog.show()
    }

    fun clickReviseContents() {
        MaterialDialog(this)
            .noAutoDismiss()
            .input(
                hint = "?????????",
                inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE,
                prefill = binding.homeProfiletext.text.toString()
            )
            .show {
                title(text = "????????? ??????")
                positiveButton(text = "??????", click = {
                    when {
                        it.getInputField().text == null ->
                            showSnackBar(binding.root, "???????????? ?????? ??? ?????????.")
                        it.getInputField().text.length > 60 ->
                            showSnackBar(binding.root, "60??? ?????? ?????? ??? ?????????.")
                        it.getInputField().lineCount > 4 ->
                            showSnackBar(binding.root, "4??? ?????? ?????? ??? ?????????.")
                        else -> {
                            binding.homeProfiletext.text = it.getInputField().text
                            it.dismiss()
                        }
                    }
                })
                negativeButton(text = "??????", click = {
                    it.dismiss()
                })
            }
    }

    fun goRecipeDetail(item: Recipe) {
        startActivity(
            Intent(this@HomeActivity, RecipeDetailActivity::class.java)
                .putExtra(Constants.INTENTNAME.RECIPE_ID, item.recipeID)
        )
    }

    fun goHome(item: Post) {
        startActivity(
            Intent(this@HomeActivity, HomeActivity::class.java)
                .putExtra(Constants.INTENTNAME.USER_ID, item.userID)
        )
    }

    fun goPostDetail(item: Post) {
        startActivity(
            Intent(this@HomeActivity, PostDetailActivity::class.java)
                .putExtra(Constants.INTENTNAME.POST_ID, item.postID)
        )
    }

    fun goMyRecipe(item: UserDetail) {
        startActivity(
            Intent(this@HomeActivity, RecipeMyListActivity::class.java)
                .putExtra(Constants.INTENTNAME.USER_ID, item.userID)
        )
    }

    fun goFollowerList(item: UserDetail) {
        startActivity(
            Intent(this@HomeActivity, FollowListActivity::class.java)
                .putExtra(Constants.INTENTNAME.USER_ID, item.userID)
                .putExtra("mode", FollowListActivity.FOLLOWER)
        )
    }

    fun goFollowingList(item: UserDetail) {
        startActivity(
            Intent(this@HomeActivity, FollowListActivity::class.java)
                .putExtra(Constants.INTENTNAME.USER_ID, item.userID)
                .putExtra("mode", FollowListActivity.FOLLOWING)
        )
    }

    companion object {
        object UIMODE{
            const val OWNER = 0
            const val OTHERS = 1
        }

        object OPERATION {
            const val VIEW = 0
            const val REVISE = 1
        }
    }
}