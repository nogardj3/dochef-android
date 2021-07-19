package com.yhjoo.dochef.activities;

import android.os.Bundle;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.adapter.FAQListAdapter;
import com.yhjoo.dochef.databinding.AFaqBinding;
import com.yhjoo.dochef.interfaces.RxRetrofitServices;
import com.yhjoo.dochef.model.ExpandContents;
import com.yhjoo.dochef.model.ExpandTitle;
import com.yhjoo.dochef.model.FAQ;
import com.yhjoo.dochef.utils.DataGenerator;
import com.yhjoo.dochef.utils.RxRetrofitBuilder;

import java.util.ArrayList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;

public class FAQActivity extends BaseActivity {
    AFaqBinding binding;
    RxRetrofitServices.BasicService basicService;
    FAQListAdapter FAQListAdapter;

    ArrayList<MultiItemEntity> faqList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AFaqBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.faqToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        basicService =
                RxRetrofitBuilder.create(this, RxRetrofitServices.BasicService.class);

        FAQListAdapter = new FAQListAdapter(faqList);
        binding.faqRecycler.setAdapter(FAQListAdapter);
        binding.faqRecycler.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (App.isServerAlive()) {
            compositeDisposable.add(
                    basicService.getFAQ()
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(response -> {
                                loadList(response.body());
                            }, RxRetrofitBuilder.defaultConsumer())
            );
        } else {
            ArrayList<FAQ> faqs = DataGenerator.make(getResources(), getResources().getInteger(R.integer.DATA_TYPE_FAQ));
            loadList(faqs);
        }
    }

    void loadList(ArrayList<FAQ> resList) {
        for (FAQ item : resList) {
            ExpandTitle title = new ExpandTitle(item.title);
            title.addSubItem(new ExpandContents(item.contents, 0));
            faqList.add(title);
        }

        FAQListAdapter.setNewData(faqList);
        FAQListAdapter.setEmptyView(R.layout.rv_empty, (ViewGroup) binding.faqRecycler.getParent());
    }
}
