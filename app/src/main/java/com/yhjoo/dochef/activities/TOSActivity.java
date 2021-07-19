package com.yhjoo.dochef.activities;

import android.os.Bundle;
import android.text.Html;

import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.databinding.ATosBinding;
import com.yhjoo.dochef.interfaces.RxRetrofitServices;
import com.yhjoo.dochef.utils.RxRetrofitBuilder;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;

public class TOSActivity extends BaseActivity {
    ATosBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ATosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (App.isServerAlive()) {
            RxRetrofitServices.BasicService basicService =
                    RxRetrofitBuilder.create(this, RxRetrofitServices.BasicService.class);

            compositeDisposable.add(
                    basicService.getTOS()
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(response -> {
                                String tos_text = response.body().get("message").toString();
                                binding.tosText.setText(Html.fromHtml(tos_text, Html.FROM_HTML_MODE_LEGACY));
                            }, RxRetrofitBuilder.defaultConsumer())
            );
        } else
            binding.tosText.setText(Html.fromHtml(getString(R.string.tos_text_dummy), Html.FROM_HTML_MODE_LEGACY));
    }
}
