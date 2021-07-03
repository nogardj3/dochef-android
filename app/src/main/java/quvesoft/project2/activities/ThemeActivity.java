package quvesoft.project2.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.ButterKnife;
import quvesoft.project2.R;
import quvesoft.project2.base.BaseActivity;
import quvesoft.project2.classes.RecipeListItem;

import static quvesoft.project2.Preferences.temprecipes;

public class ThemeActivity extends BaseActivity {
    private class ThemeItem implements MultiItemEntity {
        private int itemType;
        private int spanSize;

        private RecipeListItem content;

        ThemeItem(int itemType, int spanSize, RecipeListItem content) {
            this.itemType = itemType;
            this.spanSize = spanSize;
            this.content = content;
        }

        ThemeItem(int itemType, int spanSize) {
            this.itemType = itemType;
            this.spanSize = spanSize;
        }

        private int getSpanSize() {
            return spanSize;
        }

        private RecipeListItem getContent() {
            return content;
        }

        @Override
        public int getItemType() {
            return itemType;
        }
    }


    @BindView(R.id.theme_recycler)
    RecyclerView recyclerView;

    private RecipeListAdapter recipeListAdapter;
    private ArrayList<ThemeItem> recipeListItems = new ArrayList<>();

    private final int VIEWHOLDER_AD = 1;
    private final int VIEWHOLDER_ITEM = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_theme);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.theme_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        for (int i = 0; i <= 3; i++) {
            Random r = new Random();

            recipeListItems.add(new ThemeItem(VIEWHOLDER_ITEM, 1, new RecipeListItem("요리" + i, "만든이" + i, "메세지" + i, i, Integer.toString(temprecipes[r.nextInt(6)]))));
            recipeListItems.add(new ThemeItem(VIEWHOLDER_ITEM, 1, new RecipeListItem("요리" + i, "만든이" + i, "메세지" + i, i, Integer.toString(temprecipes[r.nextInt(6)]))));
            recipeListItems.add(new ThemeItem(VIEWHOLDER_ITEM, 1, new RecipeListItem("요리" + i, "만든이" + i, "메세지" + i, i, Integer.toString(temprecipes[r.nextInt(6)]))));
            recipeListItems.add(new ThemeItem(VIEWHOLDER_ITEM, 1, new RecipeListItem("요리" + i, "만든이" + i, "메세지" + i, i, Integer.toString(temprecipes[r.nextInt(6)]))));
            recipeListItems.add(new ThemeItem(VIEWHOLDER_ITEM, 1, new RecipeListItem("요리" + i, "만든이" + i, "메세지" + i, i, Integer.toString(temprecipes[r.nextInt(6)]))));
            recipeListItems.add(new ThemeItem(VIEWHOLDER_ITEM, 1, new RecipeListItem("요리" + i, "만든이" + i, "메세지" + i, i, Integer.toString(temprecipes[r.nextInt(6)]))));
            recipeListItems.add(new ThemeItem(VIEWHOLDER_AD, 2));
        }

        recipeListAdapter = new RecipeListAdapter(recipeListItems, Glide.with(this));
        final GridLayoutManager manager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(manager);
        recipeListAdapter.setSpanSizeLookup(new BaseQuickAdapter.SpanSizeLookup() {
            @Override
            public int getSpanSize(GridLayoutManager gridLayoutManager, int position) {
                return recipeListItems.get(position).getSpanSize();
            }
        });
        recyclerView.setAdapter(recipeListAdapter);
        recipeListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (adapter.getItemViewType(position) == VIEWHOLDER_ITEM) {
                    startActivity(new Intent(ThemeActivity.this, RecipeActivity.class));
                }
            }
        });
    }

    private class RecipeListAdapter extends BaseMultiItemQuickAdapter<ThemeItem, BaseViewHolder> {
        private RequestManager requestManager;

        RecipeListAdapter(List<ThemeItem> data, RequestManager requestManager) {
            super(data);
            addItemType(VIEWHOLDER_ITEM, R.layout.li_recipe_grid);
            addItemType(VIEWHOLDER_AD, R.layout.li_tempadview);
            this.requestManager = requestManager;
        }

        @Override
        protected void convert(BaseViewHolder helper, ThemeItem item) {
            switch (helper.getItemViewType()) {
                case VIEWHOLDER_ITEM:
                    requestManager
                            .load(Integer.valueOf(item.getContent().getRecipeImg()))
                            .apply(RequestOptions.centerCropTransform())
                            .into((AppCompatImageView) helper.getView(R.id.li_recipegrid_recipeimg));
                    helper.setText(R.id.li_recipegrid_title, item.getContent().getTitle());
                    helper.setText(R.id.li_recipegrid_nickname, "By - " + item.getContent().getNickName());
                    break;
                case VIEWHOLDER_AD:
                    MobileAds.initialize(ThemeActivity.this.getApplicationContext(), getString(R.string.admob_app_id));
                    AdView aa = helper.getView(R.id.tempadview);
                    aa.loadAd(new AdRequest.Builder()
                            .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                            .addTestDevice("261FE916C76D665A68B698D7A5F24CBF") //갤럭시5
                            .addTestDevice("1E5B6FBD0872DDC2E1F3B510504E9175") //노트2
                            .addTestDevice("7223B83422E9FEEB314276808FD76ECF") //갤럭시탭
                            .addTestDevice("974B0DAE919F69B4E73161BFF18F5AB6") //g3
                            .addTestDevice("5EEFADA7E9BDB9EABE5991DDFCA8594B") //Gpro
                            .addTestDevice("7E9FA06DCFC5D93A0A88B26846169530") //아이언1
                            .addTestDevice("2DCC82F592CEB944957E716A561951C7") //베가노트
                            .build());
                    break;
            }
        }
    }
}
