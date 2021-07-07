package com.yhjoo.dochef.activities;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.base.BaseActivity;
import com.yhjoo.dochef.classes.FAQ;
import com.yhjoo.dochef.interfaces.RetrofitServices;
import com.yhjoo.dochef.utils.DummyMaker;
import com.yhjoo.dochef.utils.RetrofitBuilder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FAQActivity extends BaseActivity {
    @BindView(R.id.faq_recycler)
    RecyclerView recyclerView;

    private final int FAQ_DEPTH_0 = 0;
    private final int FAQ_CONTENTS = 1;

    FAQListAdapter FAQListAdapter;
    ArrayList<MultiItemEntity> faqList = new ArrayList<>();

    /*
        TODO
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_faq);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.faq_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FAQListAdapter = new FAQListAdapter(faqList);
        recyclerView.setAdapter(FAQListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (App.isServerAlive())
            getListFromServer();
        else
            getListFromLocal();
    }

    private void getListFromServer() {
        RetrofitServices.BasicService basicService =
                RetrofitBuilder.create(this, RetrofitServices.BasicService.class);

        basicService.getFAQ().enqueue(new Callback<ArrayList<FAQ>>() {
            @Override
            public void onResponse(Call<ArrayList<FAQ>> call, Response<ArrayList<FAQ>> res) {
                ArrayList<FAQ> resList = res.body();
                for (FAQ item : resList) {
                    Title title = new Title(item.title);
                    title.addSubItem(new Contents(item.contents));
                    faqList.add(title);
                }
                FAQListAdapter.setNewData(faqList);
            }

            @Override
            public void onFailure(Call<ArrayList<FAQ>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void getListFromLocal() {
        ArrayList<FAQ> faqs = DummyMaker.make(getResources(), getResources().getInteger(R.integer.DUMMY_TYPE_FAQ));
        for (FAQ item : faqs) {
            Title Title = new Title(item.title);
            Title.addSubItem(new Contents(item.contents));
            faqList.add(Title);
        }
        FAQListAdapter.setNewData(faqList);
    }

    private class Title extends AbstractExpandableItem<Contents> implements MultiItemEntity {
        public String title;

        Title(String title) {
            this.title = title;
        }

        @Override
        public int getItemType() {
            return 0;
        }

        @Override
        public int getLevel() {
            return 0;
        }
    }

    private class Contents implements MultiItemEntity {
        public String text;

        Contents(String text) {
            this.text = text;
        }

        @Override
        public int getItemType() {
            return 1;
        }
    }

    private class FAQListAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {
        FAQListAdapter(List<MultiItemEntity> data) {
            super(data);
            addItemType(FAQ_DEPTH_0, R.layout.li_expand_d0);
            addItemType(FAQ_CONTENTS, R.layout.li_expand_d1);
        }

        @Override
        protected void convert(BaseViewHolder helper, MultiItemEntity item) {
            switch (helper.getItemViewType()) {
                case FAQ_DEPTH_0:
                    final Title lv0 = (Title) item;
                    helper.setText(R.id.exp_d0_title, lv0.title)
                            .setImageResource(R.id.exp_d0_icon, lv0.isExpanded() ? R.drawable.ic_arrow_downward_black_24dp : R.drawable.ic_arrow);
                    helper.itemView.setOnClickListener(v -> {
                        int pos = helper.getAbsoluteAdapterPosition();
                        if (lv0.isExpanded()) {
                            collapse(pos);
                        } else {
                            expand(pos);
                        }
                    });
                    break;

                case FAQ_CONTENTS:
                    final Contents contents = (Contents) item;
                    helper.setText(R.id.exp_d1_text, contents.text);

                    break;
            }
        }
    }
}
