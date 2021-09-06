package com.yhjoo.dochef.activities

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.adapter.NoticeListAdapter
import com.yhjoo.dochef.databinding.ANoticeBinding
import com.yhjoo.dochef.utils.RxRetrofitServices.BasicService
import com.yhjoo.dochef.data.model.ExpandContents
import com.yhjoo.dochef.data.model.ExpandTitle
import com.yhjoo.dochef.data.model.Notice
import com.yhjoo.dochef.data.DataGenerator
import com.yhjoo.dochef.ui.activities.BaseActivity
import com.yhjoo.dochef.utils.RxRetrofitBuilder
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import retrofit2.Response
import java.util.*

class NoticeActivity : BaseActivity() {
    var binding: ANoticeBinding? = null
    var basicService: BasicService? = null
    var noticeListAdapter: NoticeListAdapter? = null
    var noticeList = ArrayList<MultiItemEntity>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ANoticeBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        setSupportActionBar(binding!!.noticeToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        basicService = RxRetrofitBuilder.create(this, BasicService::class.java)
        noticeListAdapter = NoticeListAdapter(noticeList)
        binding!!.noticeRecycler.layoutManager = LinearLayoutManager(this)
        binding!!.noticeRecycler.adapter = noticeListAdapter
    }

    override fun onResume() {
        super.onResume()
        if (App.isServerAlive()) {
            compositeDisposable!!.add(
                basicService.getNotice()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ response: Response<ArrayList<Notice?>?>? ->
                        loadList(
                            response!!.body()
                        )
                    }, RxRetrofitBuilder.defaultConsumer())
            )
        } else {
            val response = DataGenerator.make<ArrayList<Notice?>>(
                resources,
                resources.getInteger(R.integer.DATA_TYPE_NOTICE)
            )
            loadList(response)
        }
    }

    fun loadList(resList: ArrayList<Notice?>?) {
        for (item in resList!!) {
            val title = ExpandTitle(item!!.title)
            title.addSubItem(ExpandContents(item.contents, 0))
            noticeList.add(title)
        }
        noticeListAdapter!!.setNewData(noticeList)
    }
}