package com.yhjoo.dochef.activities

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.adapter.FAQListAdapter
import com.yhjoo.dochef.databinding.AFaqBinding
import com.yhjoo.dochef.utils.RxRetrofitServices.BasicService
import com.yhjoo.dochef.data.model.ExpandContents
import com.yhjoo.dochef.data.model.ExpandTitle
import com.yhjoo.dochef.data.model.FAQ
import com.yhjoo.dochef.data.DataGenerator
import com.yhjoo.dochef.utils.RxRetrofitBuilder
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import retrofit2.Response
import java.util.*

class FAQActivity : BaseActivity() {
    var binding: AFaqBinding? = null
    var basicService: BasicService? = null
    var FAQListAdapter: FAQListAdapter? = null
    var faqList = ArrayList<MultiItemEntity>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AFaqBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        setSupportActionBar(binding!!.faqToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        basicService = RxRetrofitBuilder.create(this, BasicService::class.java)
        FAQListAdapter = FAQListAdapter(faqList)
        binding!!.faqRecycler.adapter = FAQListAdapter
        binding!!.faqRecycler.layoutManager = LinearLayoutManager(this)
    }

    override fun onResume() {
        super.onResume()
        if (App.isServerAlive()) {
            compositeDisposable!!.add(
                basicService.getFAQ()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ response: Response<ArrayList<FAQ?>?>? ->
                        loadList(
                            response!!.body()
                        )
                    }, RxRetrofitBuilder.defaultConsumer())
            )
        } else {
            val faqs = DataGenerator.make<ArrayList<FAQ?>>(
                resources,
                resources.getInteger(R.integer.DATA_TYPE_FAQ)
            )
            loadList(faqs)
        }
    }

    fun loadList(resList: ArrayList<FAQ?>?) {
        for (item in resList!!) {
            val title = ExpandTitle(item!!.title)
            title.addSubItem(ExpandContents(item.contents, 0))
            faqList.add(title)
        }
        FAQListAdapter!!.setNewData(faqList)
    }
}