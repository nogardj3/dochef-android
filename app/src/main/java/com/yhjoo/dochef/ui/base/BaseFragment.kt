package com.yhjoo.dochef.ui.base

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import com.yhjoo.dochef.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

open class BaseFragment : Fragment() {
    private var progressDialog: AppCompatDialog? = null

    override fun onDestroy() {
        super.onDestroy()
        hideProgress()
    }

    fun showSnackBar(view: View, text: String) {
        Snackbar.make(view, text, Snackbar.LENGTH_SHORT).show()
    }

    fun showSnackBar(view: View, text: String, actionText: String, action: ((View) -> Unit)) {
        Snackbar.make(view, text, Snackbar.LENGTH_SHORT)
            .setAction(actionText, action)
            .show()
    }

    fun showProgress(activity: Activity?) {
        progressDialog?.apply {
            dismiss()
        }

        progressDialog = AppCompatDialog(activity).apply {
            setCancelable(false)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setContentView(R.layout.view_progress)
            show()
        }
    }

    fun hideProgress() {
        progressDialog?.dismiss()
    }

    fun hideKeyboard(context: Context, view: View) {
        val inputMethodManager =
            context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun LifecycleOwner.subscribeEventOnLifecycle(block: suspend CoroutineScope.() -> Unit) {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED, block)
        }
    }
}