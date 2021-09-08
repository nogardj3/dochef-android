package com.yhjoo.dochef.ui.activities

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.DataGenerator
import com.yhjoo.dochef.data.model.ExpandContents
import com.yhjoo.dochef.data.model.ExpandTitle
import com.yhjoo.dochef.data.model.Notice
import com.yhjoo.dochef.databinding.ANoticeBinding
import com.yhjoo.dochef.ui.adapter.NoticeListAdapter
import com.yhjoo.dochef.utils.RetrofitBuilder
import com.yhjoo.dochef.utils.RetrofitServices.BasicService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class NoticeActivity : BaseActivity() {
    val binding: ANoticeBinding by lazy { ANoticeBinding.inflate(layoutInflater) }

    private lateinit var basicService: BasicService
    private lateinit var noticeListAdapter: NoticeListAdapter
    private lateinit var noticeList: ArrayList<MultiItemEntity>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.noticeToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        basicService = RetrofitBuilder.create(this, BasicService::class.java)

        noticeListAdapter = NoticeListAdapter(noticeList)
        binding.noticeRecycler.layoutManager = LinearLayoutManager(this)
        binding.noticeRecycler.adapter = noticeListAdapter
    }

    override fun onResume() {
        super.onResume()
        CoroutineScope(Dispatchers.Main).launch {
            if (App.isServerAlive) {
                val noticeResponse = withContext(Dispatchers.IO) {
                    basicService.getNotice()
                }
                loadList(noticeResponse.body()!!)

                // RXJAVA
//                compositeDisposable.add(
//                    basicService.getNotice()
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe({ response: Response<ArrayList<Notice>> ->
//                            loadList(response.body()!!)
//                        }, RetrofitBuilder.defaultConsumer())
//                )
            } else {
                val noticeResponse = withContext(Dispatchers.IO) {
                    DataGenerator.make<ArrayList<Notice>>(
                        resources,
                        resources.getInteger(R.integer.DATA_TYPE_NOTICE)
                    )
                }
                loadList(noticeResponse)
            }
        }
    }

    private fun loadList(resList: ArrayList<Notice>) {
        for (item in resList) {
            val title = ExpandTitle(item.title)
            title.addSubItem(ExpandContents(item.contents, 0))
            noticeList.add(title)
        }
        noticeListAdapter.setNewData(noticeList as List<MultiItemEntity>)
    }
}