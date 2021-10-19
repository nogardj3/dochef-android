package com.yhjoo.dochef.ui.base

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialog
import androidx.appcompat.widget.AppCompatEditText
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import com.yhjoo.dochef.R
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

open class BaseActivity : AppCompatActivity() {
    private var progressDialog: AppCompatDialog? = null
    val compositeDisposable: CompositeDisposable by lazy { CompositeDisposable() }

    override fun onDestroy() {
        super.onDestroy()
        hideProgress()
        if (!compositeDisposable.isDisposed) compositeDisposable.clear()
    }

    // navigation up == backpressed
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    // Edittext outside touch hide keyboard
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        val view = currentFocus
        val ret = super.dispatchTouchEvent(event)
        if (view is AppCompatEditText) {
            val w = currentFocus
            val scrcoords = IntArray(2)
            w!!.getLocationOnScreen(scrcoords)
            val x = event.rawX + w.left - scrcoords[0]
            val y = event.rawY + w.top - scrcoords[1]
            if (event.action == MotionEvent.ACTION_UP
                && (x < w.left || x >= w.right || y < w.top || y > w.bottom)
            ) {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(window.currentFocus!!.windowToken, 0)
            }
        }
        return ret
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
        if (activity == null || activity.isFinishing) {
            return
        }
        progressDialog?.apply {
            dismiss()
        }

        progressDialog = AppCompatDialog(this)
        progressDialog?.apply {
            setCancelable(false)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setContentView(R.layout.view_progress)
            show()
        }
    }

    fun hideProgress() {
        progressDialog?.dismiss()
    }

    fun hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun LifecycleOwner.subscribeEventOnLifecycle(block: suspend CoroutineScope.() -> Unit) {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED, block)
        }
    }
}