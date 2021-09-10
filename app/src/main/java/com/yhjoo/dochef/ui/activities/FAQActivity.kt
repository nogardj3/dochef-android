package com.yhjoo.dochef.ui.activities

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.DataGenerator
import com.yhjoo.dochef.data.model.ExpandContents
import com.yhjoo.dochef.data.model.ExpandTitle
import com.yhjoo.dochef.data.model.FAQ
import com.yhjoo.dochef.databinding.AFaqBinding
import com.yhjoo.dochef.ui.adapter.FAQListAdapter
import com.yhjoo.dochef.utils.RetrofitBuilder
import com.yhjoo.dochef.utils.RetrofitServices.BasicService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class FAQActivity : BaseActivity() {
    val binding: AFaqBinding by lazy { AFaqBinding.inflate(layoutInflater) }

    private lateinit var basicService: BasicService
    private lateinit var faqListAdapter: FAQListAdapter
    private var faqList = ArrayList<MultiItemEntity>()

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

        CoroutineScope(Dispatchers.Main).launch {
            runCatching {
                if (App.isServerAlive) {
                    val faqResponse = basicService.getFAQ()
                    loadList(faqResponse.body()!!)
                } else {
                    val faqResponse = withContext(Dispatchers.IO) {
                        DataGenerator.make<ArrayList<FAQ>>(
                            resources,
                            resources.getInteger(R.integer.DATA_TYPE_FAQ)
                        )
                    }
                    loadList(faqResponse)
                }
            }
                .onSuccess {}
                .onFailure {
                    RetrofitBuilder.defaultErrorHandler(it)
                }
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