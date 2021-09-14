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
import com.yhjoo.dochef.data.model.ExpandContents
import com.yhjoo.dochef.data.model.ExpandTitle
import com.yhjoo.dochef.data.model.FAQ
import com.yhjoo.dochef.data.model.RecipePhase
import com.yhjoo.dochef.databinding.AFaqBinding
import com.yhjoo.dochef.databinding.FPlayrecipeItemBinding
import com.yhjoo.dochef.databinding.FSettingFaqBinding
import com.yhjoo.dochef.ui.adapter.FAQListAdapter
import com.yhjoo.dochef.utils.ImageLoadUtil
import com.yhjoo.dochef.utils.RetrofitBuilder
import com.yhjoo.dochef.utils.RetrofitServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.ArrayList

class SettingFAQFragment : Fragment() {
    private lateinit var binding: FSettingFaqBinding
    private lateinit var basicService: RetrofitServices.BasicService
    private lateinit var faqListAdapter: FAQListAdapter
    private var faqList = ArrayList<MultiItemEntity>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FSettingFaqBinding.inflate(inflater, container, false)
        val view: View = binding.root


        basicService = RetrofitBuilder.create(requireContext(), RetrofitServices.BasicService::class.java)
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