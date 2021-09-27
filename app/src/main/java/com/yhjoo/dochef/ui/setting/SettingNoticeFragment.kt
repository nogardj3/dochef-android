package com.yhjoo.dochef.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.databinding.SettingNoticeFragmentBinding
import com.yhjoo.dochef.data.DataGenerator
import com.yhjoo.dochef.data.model.ExpandContents
import com.yhjoo.dochef.data.model.ExpandTitle
import com.yhjoo.dochef.data.model.Notice
import com.yhjoo.dochef.data.network.RetrofitBuilder
import com.yhjoo.dochef.data.network.RetrofitServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class SettingNoticeFragment : Fragment() {
    private lateinit var binding: SettingNoticeFragmentBinding
    private lateinit var basicService: RetrofitServices.BasicService
    private lateinit var expandableNoticeListAdapter: ExpandableNoticeListAdapter
    private var noticeList = ArrayList<MultiItemEntity>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SettingNoticeFragmentBinding.inflate(inflater, container, false)
        val view: View = binding.root

        basicService =
            RetrofitBuilder.create(requireContext(), RetrofitServices.BasicService::class.java)
        expandableNoticeListAdapter = ExpandableNoticeListAdapter(noticeList)
        binding.apply {
            noticeRecycler.adapter = expandableNoticeListAdapter
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
        expandableNoticeListAdapter.setNewData(noticeList as List<MultiItemEntity>)
    }
}