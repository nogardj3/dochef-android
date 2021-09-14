package com.yhjoo.dochef.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.yhjoo.dochef.databinding.AccountFindpwFragmentBinding

class AccountFindPWFragment : Fragment() {
    private lateinit var binding: AccountFindpwFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AccountFindpwFragmentBinding.inflate(inflater, container, false)


        return binding.root
    }
}