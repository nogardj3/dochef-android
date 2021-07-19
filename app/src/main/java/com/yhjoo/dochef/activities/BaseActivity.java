package com.yhjoo.dochef.activities;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.widget.AppCompatEditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.yhjoo.dochef.R;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class BaseActivity extends AppCompatActivity {
    AppCompatDialog progressDialog;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    public CompositeDisposable getCompositeDisposable() {
        return compositeDisposable;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        View view = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

        if (view instanceof AppCompatEditText) {
            View w = getCurrentFocus();
            int[] scrcoords = new int[2];
            w.getLocationOnScreen(scrcoords);
            float x = event.getRawX() + w.getLeft() - scrcoords[0];
            float y = event.getRawY() + w.getTop() - scrcoords[1];

            if (event.getAction() == MotionEvent.ACTION_UP
                    && (x < w.getLeft() || x >= w.getRight()
                    || y < w.getTop() || y > w.getBottom())) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
            }
        }
        return ret;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressOFF();
        if (compositeDisposable != null && !compositeDisposable.isDisposed())
            compositeDisposable.clear();

    }

    void progressON(Activity activity) {

        if (activity == null || activity.isFinishing()) {
            return;
        }


        if (progressDialog == null || !progressDialog.isShowing()) {
            progressDialog = new AppCompatDialog(activity);
            progressDialog.setCancelable(false);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.v_progress);
            progressDialog.show();

        }
    }

    void progressOFF() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public static MaterialDialog createConfirmDialog(Context context, String title, String content,
                                                     MaterialDialog.SingleButtonCallback confirmListener) {

        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .positiveColorRes(R.color.grey_text)
                .negativeColorRes(R.color.grey_text)
                .titleColorRes(R.color.black)
                .contentColorRes(R.color.black)
                .positiveText("확인")
                .negativeText("취소")
                .onPositive(confirmListener)
                .build();

        if (title != null && !title.equals(""))
            dialog.setTitle(title);
        if (content != null && !content.equals(""))
            dialog.setContent(content);

        return dialog;
    }
}
