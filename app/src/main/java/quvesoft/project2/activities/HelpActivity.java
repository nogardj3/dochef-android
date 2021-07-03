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

public class HelpActivity extends BaseActivity {
    @BindView(R.id.help_recycler)
    RecyclerView recyclerView;

    private ArrayList<MultiItemEntity> announces = new ArrayList<>();

    private final int HELP_DEPTH_0 = 0;
    private final int HELP_CONTENTS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_help);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.help_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        for (int i = 0; i < 10; i++) {
            Title Title = new Title("자주묻는 질문" + i);
            Title.addSubItem(new Contents("자\n주\n묻\n는\n질\n문" + i));
            announces.add(Title);
        }

        HelpListAdapter helpListAdapter = new HelpListAdapter(announces);
        recyclerView.setAdapter(helpListAdapter);
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

    private class HelpListAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {
        HelpListAdapter(List<MultiItemEntity> data) {
            super(data);
            addItemType(HELP_DEPTH_0, R.layout.exp_help0);
            addItemType(HELP_CONTENTS, R.layout.exp_help1);
        }

        @Override
        protected void convert(BaseViewHolder helper, MultiItemEntity item) {
            switch (helper.getItemViewType()) {
                case HELP_DEPTH_0:
                    final Title lv0 = (Title) item;
                    helper.setText(R.id.exp_help0_title, lv0.title)
                            .setImageResource(R.id.exp_help0_icon, lv0.isExpanded() ? R.drawable.ic_arrow_downward_black_24dp : R.drawable.ic_arrow);
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

                case HELP_CONTENTS:
                    final Contents contents = (Contents) item;
                    helper.setText(R.id.exp_help1_text, contents.text);

                    break;
            }
        }
    }
}
