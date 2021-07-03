package quvesoft.project2.activities;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import quvesoft.project2.R;
import quvesoft.project2.base.BaseActivity;

public class MakeRecipeActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_makerecipe);

        Toolbar toolbar = (Toolbar) findViewById(R.id.makerecipe_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}