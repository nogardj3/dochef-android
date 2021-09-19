package com.yhjoo.dochef.ui.fragments

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.yhjoo.dochef.App
import com.yhjoo.dochef.databinding.SettingTosFragmentBinding
import com.yhjoo.dochef.utilities.RetrofitBuilder
import com.yhjoo.dochef.utilities.RetrofitServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingTosFragment : Fragment() {
    private lateinit var binding: SettingTosFragmentBinding
    private lateinit var basicService: RetrofitServices.BasicService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SettingTosFragmentBinding.inflate(inflater, container, false)
        val view: View = binding.root

        basicService =
            RetrofitBuilder.create(requireContext(), RetrofitServices.BasicService::class.java)

        return view
    }

    override fun onResume() {
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

        super.onResume()
    }
}