package com.yhjoo.dochef.activities;

import android.os.Bundle;
import android.text.Html;

import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.databinding.ATosBinding;
import com.yhjoo.dochef.interfaces.RetrofitServices;
import com.yhjoo.dochef.utils.BasicCallback;
import com.yhjoo.dochef.utils.RetrofitBuilder;

import retrofit2.Call;
import retrofit2.Response;

public class TOSActivity extends BaseActivity {
    ATosBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ATosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (App.isServerAlive()) {
            RetrofitServices.BasicService basicService =
                    RetrofitBuilder.create(this, RetrofitServices.BasicService.class);

            basicService.getTOS()
                    .enqueue(new BasicCallback<String>(this) {
                        @Override
                        public void onResponse(Call<String> call, Response<String> res) {
                            String tos_text = res.body();
                            binding.tosText.setText(Html.fromHtml(tos_text, Html.FROM_HTML_MODE_LEGACY));
                        }
                    });
        } else
            binding.tosText.setText(Html.fromHtml(getString(R.string.tos_text_dummy), Html.FROM_HTML_MODE_LEGACY));
    }
}
