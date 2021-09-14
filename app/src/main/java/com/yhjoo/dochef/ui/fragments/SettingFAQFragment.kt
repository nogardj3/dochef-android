package com.yhjoo.dochef.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.databinding.SettingFaqFragmentBinding
import com.yhjoo.dochef.db.DataGenerator
import com.yhjoo.dochef.model.ExpandContents
import com.yhjoo.dochef.model.ExpandTitle
import com.yhjoo.dochef.model.FAQ
import com.yhjoo.dochef.ui.adapter.FAQListAdapter
import com.yhjoo.dochef.utilities.RetrofitBuilder
import com.yhjoo.dochef.utilities.RetrofitServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class SettingFAQFragment : Fragment() {
    private lateinit var binding: SettingFaqFragmentBinding
    private lateinit var basicService: RetrofitServices.BasicService
    private lateinit var faqListAdapter: FAQListAdapter
    private var faqList = ArrayList<MultiItemEntity>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SettingFaqFragmentBinding.inflate(inflater, container, false)
        val view: View = binding.root

        basicService =
            RetrofitBuilder.create(requireContext(), RetrofitServices.BasicService::class.java)
        faqListAdapter = FAQListAdapter(faqList)
        binding.apply {
            faqRecycler.adapter = faqListAdapter
            faqRecycler.layoutManager = LinearLayoutManager(requireContext())
        }

        return view
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