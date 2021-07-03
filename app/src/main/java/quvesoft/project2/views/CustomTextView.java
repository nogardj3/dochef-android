package quvesoft.project2.views;

import android.content.Context;
import android.util.AttributeSet;

public class CustomTextView extends AppCompatTextView {
    public CustomTextView(Context context) {
        super(context);
        initview();
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initview();
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initview();
    }

    private void initview() {
        setPadding(0, 0, 7, 0);
        setTextSize(16);
    }
}
