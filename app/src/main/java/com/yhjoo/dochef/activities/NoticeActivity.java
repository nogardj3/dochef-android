package com.yhjoo.dochef.activities;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.base.BaseActivity;
import com.yhjoo.dochef.classes.Notice;
import com.yhjoo.dochef.databinding.ANoticeBinding;
import com.yhjoo.dochef.interfaces.RetrofitServices;
import com.yhjoo.dochef.utils.DummyMaker;
import com.yhjoo.dochef.utils.RetrofitBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NoticeActivity extends BaseActivity {
    private final int NOTICE_DEPTH_0 = 0;
    private final int NOTICE_CONTENTS = 1;

    ANoticeBinding binding;

    ArrayList<MultiItemEntity> noticeList = new ArrayList<>();
    NoticeListAdapter noticeListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ANoticeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.noticeToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        noticeListAdapter = new NoticeListAdapter(noticeList);
        binding.noticeRecycler.setAdapter(noticeListAdapter);
        binding.noticeRecycler.setLayoutManager(new LinearLayoutManager(this));

        if (App.isServerAlive())
            getListFromServer();
        else
            getListFromDummy();
    }

    void getListFromServer() {
        RetrofitServices.BasicService basicService =
                RetrofitBuilder.create(this, RetrofitServices.BasicService.class);

        basicService.getNotice().enqueue(new Callback<ArrayList<Notice>>() {
            @Override
            public void onResponse(Call<ArrayList<Notice>> call, Response<ArrayList<Notice>> res) {
                ArrayList<Notice> resList = res.body();
                for (Notice item : resList) {
                    Title title = new Title(item.title);
                    title.addSubItem(new Contents(item.contents));
                    noticeList.add(title);
                }

                noticeListAdapter.setNewData(noticeList);
            }

            @Override
            public void onFailure(Call<ArrayList<Notice>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    void getListFromDummy(){
        ArrayList<Notice> response = DummyMaker.make(getResources(), getResources().getInteger(R.integer.DUMMY_TYPE_NOTICE));
        for (Notice item : response) {
            Title title = new Title(item.title);
            title.addSubItem(new Contents(item.contents));
            noticeList.add(title);
        }
        noticeListAdapter.setNewData(noticeList);
    }

    class Title extends AbstractExpandableItem<Contents> implements MultiItemEntity {
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

    class Contents implements MultiItemEntity {
        public String text;

        Contents(String text) {
            this.text = text;
        }

        @Override
        public int getItemType() {
            return 1;
        }
    }

    class NoticeListAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {
        NoticeListAdapter(List<MultiItemEntity> data) {
            super(data);
            addItemType(NOTICE_DEPTH_0, R.layout.li_expand_d0);
            addItemType(NOTICE_CONTENTS, R.layout.li_expand_d1);
        }

        @Override
        protected void convert(BaseViewHolder helper, MultiItemEntity item) {
            switch (helper.getItemViewType()) {
                case NOTICE_DEPTH_0:
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

                case NOTICE_CONTENTS:
                    final Contents contents = (Contents) item;
                    helper.setText(R.id.exp_d1_text, contents.text);

                    break;
            }
        }
    }
}
