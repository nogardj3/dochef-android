package com.yhjoo.dochef.activities;

import android.os.Bundle;
import android.text.Html;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.base.BaseActivity;
import com.yhjoo.dochef.interfaces.RetrofitServices;
import com.yhjoo.dochef.utils.RetrofitBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TOSActivity extends BaseActivity {

    /*
        TODO
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_tos);

        Toolbar toolbar = (Toolbar) findViewById(R.id.base_toolbar);
        toolbar.setTitle("이용약관");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (App.isServerAlive()) {
            RetrofitServices.BasicService basicService =
                    RetrofitBuilder.createScalar(this, RetrofitServices.BasicService.class);

            basicService.getTOS().enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> res) {
                    String tos_text = res.body();
                    ((AppCompatTextView) findViewById(R.id.tos_text)).setText(
                            Html.fromHtml(tos_text, Html.FROM_HTML_MODE_LEGACY));
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        } else {
            ((AppCompatTextView) findViewById(R.id.tos_text)).setText(
                    Html.fromHtml(getString(R.string.tos_text), Html.FROM_HTML_MODE_LEGACY));
        }
    }
}
