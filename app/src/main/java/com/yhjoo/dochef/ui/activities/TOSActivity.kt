package com.yhjoo.dochef.activities

import android.os.Bundle
import android.text.Html
import com.google.gson.JsonObject
import com.yhjoo.dochef.App
import com.yhjoo.dochef.databinding.ATosBinding
import com.yhjoo.dochef.utils.RxRetrofitServices.BasicService
import com.yhjoo.dochef.utils.RxRetrofitBuilder
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import retrofit2.Response

class TOSActivity : BaseActivity() {
    var binding: ATosBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ATosBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        setSupportActionBar(binding!!.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onResume() {
        super.onResume()
        if (App.isServerAlive()) {
            val basicService = RxRetrofitBuilder.create(this, BasicService::class.java)
            compositeDisposable!!.add(
                basicService.tos
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ response: Response<JsonObject?>? ->
                        val tos_text = response!!.body()!!["message"].asString
                        binding!!.tosText.text = Html.fromHtml(tos_text, Html.FROM_HTML_MODE_LEGACY)
                    }, RxRetrofitBuilder.defaultConsumer())
            )
        } else binding!!.tosText.text = "이용약관"
    }
}