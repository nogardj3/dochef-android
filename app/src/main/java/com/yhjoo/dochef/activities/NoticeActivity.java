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
import com.yhjoo.dochef.classes.Notice;
import com.yhjoo.dochef.utils.DummyMaker;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NoticeActivity extends BaseActivity {
    private final int NOTICE_DEPTH_0 = 0;
    private final int NOTICE_CONTENTS = 1;
    private final ArrayList<MultiItemEntity> announces = new ArrayList<>();
    @BindView(R.id.notice_recycler)
    RecyclerView recyclerView;

    /*
        TODO
        1. retrofit 구현
        2. expandable 클래스로 빼보기
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_notice);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.notice_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (App.isServerAlive()) {
        } else {
            ArrayList<Notice> response = DummyMaker.make(getResources(), getResources().getInteger(R.integer.DUMMY_TYPE_NOTICE));
            for (Notice item : response) {
                Title Title = new Title(item.title);
                Title.addSubItem(new Contents(item.contents));
                announces.add(Title);
            }
        }

        NoticeListAdapter noticeListAdapter = new NoticeListAdapter(announces);
        recyclerView.setAdapter(noticeListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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

    private class NoticeListAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {
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
