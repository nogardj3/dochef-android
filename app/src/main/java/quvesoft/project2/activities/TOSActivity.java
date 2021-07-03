package quvesoft.project2.activities;

import android.os.Bundle;
import android.text.Html;

import quvesoft.project2.R;
import quvesoft.project2.base.BaseActivity;

public class TOSActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_tos);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tos_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ((AppCompatTextView) findViewById(R.id.tos_text)).setText(Html.fromHtml(getString(R.string.tos_text)));
    }
}
