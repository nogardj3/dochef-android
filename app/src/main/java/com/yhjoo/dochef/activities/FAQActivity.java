package com.yhjoo.dochef.activities;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.adapter.FAQListAdapter;
import com.yhjoo.dochef.databinding.AFaqBinding;
import com.yhjoo.dochef.interfaces.RetrofitServices;
import com.yhjoo.dochef.model.ExpandContents;
import com.yhjoo.dochef.model.ExpandTitle;
import com.yhjoo.dochef.model.FAQ;
import com.yhjoo.dochef.utils.BasicCallback;
import com.yhjoo.dochef.utils.DummyMaker;
import com.yhjoo.dochef.utils.RetrofitBuilder;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

public class FAQActivity extends BaseActivity {
    AFaqBinding binding;
    RetrofitServices.BasicService basicService;
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
                RetrofitBuilder.create(this, RetrofitServices.BasicService.class);

        FAQListAdapter = new FAQListAdapter(faqList);

        if (App.isServerAlive())
            getListFromServer();
        else
            getListFromLocal();

        binding.faqRecycler.setAdapter(FAQListAdapter);
        binding.faqRecycler.setLayoutManager(new LinearLayoutManager(this));
    }

    void getListFromServer() {
        basicService.getFAQ()
                .enqueue(new BasicCallback<ArrayList<FAQ>>(this) {
                    @Override
                    public void onResponse(Call<ArrayList<FAQ>> call, Response<ArrayList<FAQ>> res) {
                        ArrayList<FAQ> resList = res.body();
                        for (FAQ item : resList) {
                            ExpandTitle title = new ExpandTitle(item.title);
                            title.addSubItem(new ExpandContents(item.contents));
                            faqList.add(title);
                        }

                        FAQListAdapter.setNewData(faqList);
                    }
                });
    }

    void getListFromLocal() {
        ArrayList<FAQ> faqs = DummyMaker.make(getResources(), getResources().getInteger(R.integer.DUMMY_TYPE_FAQ));
        for (FAQ item : faqs) {
            ExpandTitle title = new ExpandTitle(item.title);
            ExpandContents contents = new ExpandContents(item.contents);
            title.addSubItem(contents);
            faqList.add(title);
        }
        FAQListAdapter.setNewData(faqList);
    }
}
