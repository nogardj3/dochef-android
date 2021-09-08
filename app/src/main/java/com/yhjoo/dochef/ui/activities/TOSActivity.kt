package com.yhjoo.dochef.ui.activities

import android.os.Bundle
import android.text.Html
import com.google.gson.JsonObject
import com.yhjoo.dochef.App
import com.yhjoo.dochef.databinding.ATosBinding
import com.yhjoo.dochef.utils.RetrofitBuilder
import com.yhjoo.dochef.utils.RetrofitServices.BasicService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import retrofit2.Response

class TOSActivity : BaseActivity() {
    private val binding: ATosBinding by lazy { ATosBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onResume() {
        super.onResume()
        if (App.isServerAlive) {
            val basicService = RetrofitBuilder.create(this, BasicService::class.java)
            compositeDisposable.add(
                basicService.tos
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ response: Response<JsonObject> ->
                        val tosText = response.body()!!["message"].asString
                        binding.tosText.text = Html.fromHtml(tosText, Html.FROM_HTML_MODE_LEGACY)
                    }, RetrofitBuilder.defaultConsumer())
            )
        } else binding.tosText.text = "이용약관"
    }
}