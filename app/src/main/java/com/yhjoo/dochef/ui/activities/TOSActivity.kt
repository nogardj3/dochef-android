package com.yhjoo.dochef.ui.activities

import android.os.Bundle
import android.text.Html
import com.google.gson.JsonObject
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.DataGenerator
import com.yhjoo.dochef.data.model.FAQ
import com.yhjoo.dochef.databinding.ATosBinding
import com.yhjoo.dochef.utils.RetrofitBuilder
import com.yhjoo.dochef.utils.RetrofitServices.BasicService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.util.ArrayList

class TOSActivity : BaseActivity() {
    private val binding: ATosBinding by lazy { ATosBinding.inflate(layoutInflater) }

    private lateinit var basicService: BasicService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        basicService = RetrofitBuilder.create(this, BasicService::class.java)
    }

    override fun onResume() {
        super.onResume()

        CoroutineScope(Dispatchers.Main).launch {
            runCatching {
                if (App.isServerAlive) {
                    val tosResponse = basicService.getTOS()
                    val tosText = tosResponse.body()!!["message"].asString
                    binding.tosText.text = Html.fromHtml(tosText, Html.FROM_HTML_MODE_LEGACY)
                } else
                    binding.tosText.text = "이용약관"
            }
                .onSuccess {}
                .onFailure {
                    RetrofitBuilder.defaultErrorHandler(it)
                }
        }
    }
}