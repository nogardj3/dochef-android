package com.yhjoo.dochef.ui.fragments

import android.os.Bundle
import android.text.Html
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
import com.yhjoo.dochef.data.model.Notice
import com.yhjoo.dochef.data.model.RecipePhase
import com.yhjoo.dochef.databinding.FPlayrecipeItemBinding
import com.yhjoo.dochef.databinding.FSettingNoticeBinding
import com.yhjoo.dochef.databinding.FSettingTosBinding
import com.yhjoo.dochef.ui.adapter.NoticeListAdapter
import com.yhjoo.dochef.utils.ImageLoadUtil
import com.yhjoo.dochef.utils.RetrofitBuilder
import com.yhjoo.dochef.utils.RetrofitServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.ArrayList

class SettingTosFragment : Fragment() {
    private lateinit var binding: FSettingTosBinding
    private lateinit var basicService: RetrofitServices.BasicService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FSettingTosBinding.inflate(inflater, container, false)
        val view: View = binding.root

        basicService = RetrofitBuilder.create(requireContext(), RetrofitServices.BasicService::class.java)

        return view
    }

    override fun onResume() {
        super.onResume()

        CoroutineScope(Dispatchers.Main).launch {
            runCatching {
                if (App.isServerAlive) {
                    val tosResponse = basicService.getTOS()
                    val tosText = tosResponse.body()!!["message"].asString
                    binding.tosText.text = Html.fromHtml(tosText, Html.FROM_HTML_MODE_LEGACY)
                } else
                    binding.tosText.text = "이용약관"
            }
                .onSuccess {}
                .onFailure {
                    RetrofitBuilder.defaultErrorHandler(it)
                }
        }
    }
}