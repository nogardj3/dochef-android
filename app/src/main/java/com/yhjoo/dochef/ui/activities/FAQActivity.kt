package com.yhjoo.dochef.ui.activities

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.ui.adapter.FAQListAdapter
import com.yhjoo.dochef.data.DataGenerator
import com.yhjoo.dochef.data.model.ExpandContents
import com.yhjoo.dochef.data.model.ExpandTitle
import com.yhjoo.dochef.data.model.FAQ
import com.yhjoo.dochef.databinding.AFaqBinding
import com.yhjoo.dochef.utils.RetrofitBuilder
import com.yhjoo.dochef.utils.RetrofitServices.BasicService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import retrofit2.Response
import java.util.*

class FAQActivity : BaseActivity() {
    val binding: AFaqBinding by lazy { AFaqBinding.inflate(layoutInflater) }

    private lateinit var basicService: BasicService
    private lateinit var faqListAdapter: FAQListAdapter
    private lateinit var faqList: ArrayList<MultiItemEntity>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.faqToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        basicService = RetrofitBuilder.create(this, BasicService::class.java)
        faqListAdapter = FAQListAdapter(faqList)
        binding.apply {
            faqRecycler.adapter = faqListAdapter
            faqRecycler.layoutManager = LinearLayoutManager(this@FAQActivity)
        }
    }

    override fun onResume() {
        super.onResume()
        if (App.isServerAlive) {
            compositeDisposable.add(
                basicService.faq
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ response: Response<ArrayList<FAQ>> ->
                        loadList(
                            response.body()!!
                        )
                    }, RetrofitBuilder.defaultConsumer())
            )
        } else {
            loadList(
                DataGenerator.make(
                    resources,
                    resources.getInteger(R.integer.DATA_TYPE_FAQ)
                )
            )
        }
    }

    private fun loadList(resList: ArrayList<FAQ>) {
        for (item in resList) {
            val title = ExpandTitle(item.title)
            title.addSubItem(ExpandContents(item.contents, 0))
            faqList.add(title)
        }
        faqListAdapter.setNewData(faqList as List<MultiItemEntity?>?)
    }
}