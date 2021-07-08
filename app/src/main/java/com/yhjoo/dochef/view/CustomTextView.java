package com.yhjoo.dochef.view;

import android.content.Context;
import android.graphics.Typeface;

import androidx.appcompat.widget.AppCompatTextView;

public class CustomTextView extends AppCompatTextView {
    Context context;

    public CustomTextView(Context context, int color) {
        super(context);
        this.context = context;
        initview(color);
    }

    private void initview(int color) {
        setPadding(0, 0, 7, 0);
        setTextSize(16);
        setTextColor(color);
        setTypeface(getTypeface(), Typeface.BOLD);
    }
}
