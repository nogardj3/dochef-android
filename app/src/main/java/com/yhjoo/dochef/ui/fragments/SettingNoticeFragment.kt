package com.yhjoo.dochef.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.DataGenerator
import com.yhjoo.dochef.data.model.*
import com.yhjoo.dochef.databinding.FPlayrecipeItemBinding
import com.yhjoo.dochef.databinding.FSettingFaqBinding
import com.yhjoo.dochef.databinding.FSettingNoticeBinding
import com.yhjoo.dochef.ui.adapter.FAQListAdapter
import com.yhjoo.dochef.ui.adapter.NoticeListAdapter
import com.yhjoo.dochef.utils.ImageLoadUtil
import com.yhjoo.dochef.utils.RetrofitBuilder
import com.yhjoo.dochef.utils.RetrofitServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.ArrayList

class SettingNoticeFragment : Fragment() {
    private lateinit var binding: FSettingNoticeBinding
    private lateinit var basicService: RetrofitServices.BasicService
    private lateinit var noticeListAdapter: NoticeListAdapter
    private var noticeList =  ArrayList<MultiItemEntity>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FSettingNoticeBinding.inflate(inflater, container, false)
        val view: View = binding.root

        basicService = RetrofitBuilder.create(requireContext(), RetrofitServices.BasicService::class.java)
        noticeListAdapter = NoticeListAdapter(noticeList)
        binding.apply {
            noticeRecycler.adapter = noticeListAdapter
            noticeRecycler.layoutManager = LinearLayoutManager(requireContext())
        }

        return view
    }

    override fun onResume() {
        super.onResume()

        CoroutineScope(Dispatchers.Main).launch {
            runCatching {
                if (App.isServerAlive) {
                    val noticeResponse = basicService.getNotice()
                    loadList(noticeResponse.body()!!)
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
                .onSuccess { }
                .onFailure {
                    RetrofitBuilder.defaultErrorHandler(it)
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