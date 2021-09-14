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
import com.yhjoo.dochef.databinding.FAccountFindpwBinding
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

class AccountFindPWFragment : Fragment() {
    private lateinit var binding: FAccountFindpwBinding
    private lateinit var basicService: RetrofitServices.BasicService
    private lateinit var faqListAdapter: FAQListAdapter
    private var faqList = ArrayList<MultiItemEntity>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FAccountFindpwBinding.inflate(inflater, container, false)
        val view: View = binding.root


        return view
    }
}