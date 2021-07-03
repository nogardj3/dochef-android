package quvesoft.project2.activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import quvesoft.project2.R;
import quvesoft.project2.base.BaseActivity;

public class NoticeActivity extends BaseActivity {
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

    @BindView(R.id.notice_recycler)
    RecyclerView recyclerView;

    private NoticeListAdapter noticeListAdapter;

    private ArrayList<MultiItemEntity> announces = new ArrayList<>();

    private final int NOTICE_DEPTH_0 = 0;
    private final int NOTICE_CONTENTS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_notice);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.notice_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        for (int i = 0; i < 10; i++) {
            Title Title = new Title("공지사항" + i);
            Title.addSubItem(new Contents("공\n지\n사\n항" + i));
            announces.add(Title);
        }

        noticeListAdapter = new NoticeListAdapter(announces);
        recyclerView.setAdapter(noticeListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private class NoticeListAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {
        NoticeListAdapter(List<MultiItemEntity> data) {
            super(data);
            addItemType(NOTICE_DEPTH_0, R.layout.exp_notice0);
            addItemType(NOTICE_CONTENTS, R.layout.exp_notice1);
        }

        @Override
        protected void convert(BaseViewHolder helper, MultiItemEntity item) {
            switch (helper.getItemViewType()) {
                case NOTICE_DEPTH_0:
                    final Title lv0 = (Title) item;
                    helper.setText(R.id.exp_notice0_title, lv0.title)
                            .setImageResource(R.id.exp_notice0_icon, lv0.isExpanded() ? R.drawable.ic_arrow_downward_black_24dp : R.drawable.ic_arrow);
                    helper.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int pos = helper.getAdapterPosition();
                            if (lv0.isExpanded()) {
                                collapse(pos);
                            } else {
                                expand(pos);
                            }
                        }
                    });
                    break;

                case NOTICE_CONTENTS:
                    final Contents contents = (Contents) item;
                    helper.setText(R.id.exp_notice1_text, contents.text);

                    break;
            }
        }
    }
}
