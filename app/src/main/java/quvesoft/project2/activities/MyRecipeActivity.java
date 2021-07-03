package quvesoft.project2.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.animation.AlphaInAnimation;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemDragListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import quvesoft.project2.R;
import quvesoft.project2.base.BaseActivity;
import quvesoft.project2.classes.RecipeListItem;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import static quvesoft.project2.Preferences.temprecipes;

public class MyRecipeActivity extends BaseActivity {
    @BindView(R.id.a_myrecipe_recycler)
    RecyclerView recyclerView;
    @BindView(R.id.a_myrecipe_alignbutton)
    AppCompatImageView alignModeIcon;

    private RecipeListAdapter recipeListAdapter;
    private ArrayList<RecipeListItem> recipeListItems = new ArrayList<>();

    private ItemTouchHelper dragitemTouchHelper;

    public boolean AlignMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_myrecipe);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.a_myrecipe_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        for (int i = 0; i < 3; i++) {
            Random r = new Random();

            recipeListItems.add(new RecipeListItem("요리1", "만든이", "메세지1", 23424, Integer.toString(temprecipes[r.nextInt(6)]), new ArrayList<>(), new ArrayList<>()));
            recipeListItems.add(new RecipeListItem("요리2", "나", "메세지2", 23424, Integer.toString(temprecipes[r.nextInt(6)]), new ArrayList<>(), new ArrayList<>()));
            recipeListItems.add(new RecipeListItem("요리1", "만든이", "메세지1", 23424, Integer.toString(temprecipes[r.nextInt(6)]), new ArrayList<>(), new ArrayList<>()));
            recipeListItems.add(new RecipeListItem("요리1", "만든이", "메세지1", 23424, Integer.toString(temprecipes[r.nextInt(6)]), new ArrayList<>(), new ArrayList<>()));
            recipeListItems.add(new RecipeListItem("요리1", "만든이", "메세지1", 23424, Integer.toString(temprecipes[r.nextInt(6)]), new ArrayList<>(), new ArrayList<>()));
            recipeListItems.add(new RecipeListItem("요리1", "만든이", "메세지1", 23424, Integer.toString(temprecipes[r.nextInt(6)]), new ArrayList<>(), new ArrayList<>()));
            recipeListItems.add(new RecipeListItem("요리1", "만든이", "메세지1", 23424, Integer.toString(temprecipes[r.nextInt(6)]), new ArrayList<>(), new ArrayList<>()));
        }


        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recipeListAdapter = new RecipeListAdapter(Glide.with(this));
        recipeListAdapter.setEmptyView(R.layout.rv_loading, (ViewGroup) recyclerView.getParent());
        recyclerView.setAdapter(recipeListAdapter);

        ItemDragAndSwipeCallback itemDragAndSwipeCallback = new ItemDragAndSwipeCallback(recipeListAdapter);
        dragitemTouchHelper = new ItemTouchHelper(itemDragAndSwipeCallback);
        dragitemTouchHelper.attachToRecyclerView(recyclerView);
        recipeListAdapter.setOnItemDragListener(new OnItemDragListener() {
            @Override
            public void onItemDragStart(RecyclerView.ViewHolder viewHolder, int pos) {
                BaseViewHolder holder = ((BaseViewHolder) viewHolder);
                holder.itemView.setAlpha(0.7f);
            }

            @Override
            public void onItemDragMoving(RecyclerView.ViewHolder source, int from, RecyclerView.ViewHolder target, int to) {
            }

            @Override
            public void onItemDragEnd(RecyclerView.ViewHolder viewHolder, int pos) {
                BaseViewHolder holder = ((BaseViewHolder) viewHolder);
                holder.itemView.setAlpha(1f);
            }
        });

        recipeListAdapter.setEmptyView(R.layout.rv_empty, (ViewGroup) recyclerView.getParent());
        recipeListAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.li_a_myrecipe_revise) {
                    startActivity(new Intent(new Intent(MyRecipeActivity.this, ReviseRecipeActivity.class)));
                } else if (view.getId() == R.id.li_a_myrecipe_delete) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MyRecipeActivity.this);
                    builder.setMessage("삭제하시겠습니까?")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    adapter.getData().remove(position);
                                    adapter.notifyItemRemoved(position);
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!AlignMode) {
            super.onBackPressed();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(MyRecipeActivity.this);
            builder.setMessage("적용하시겠습니까?")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            recipeListAdapter.disableDragItem();
                            ((AppCompatImageView) MyRecipeActivity.this.findViewById(R.id.a_myrecipe_alignbutton)).setImageResource(R.drawable.ic_low_priority_white_24dp);
                            recipeListAdapter.notifyDataSetChanged();
                            recyclerView.getLayoutManager().scrollToPosition(0);
                            AlignMode = !AlignMode;
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    }

    @OnClick(R.id.a_myrecipe_alignbutton)
    public void Moveset(View v) {
        if (!AlignMode) {
            recipeListAdapter.enableDragItem(dragitemTouchHelper);
            ((AppCompatImageView) v).setImageResource(R.drawable.ic_check_black_24dp);
            recipeListAdapter.notifyDataSetChanged();
            recyclerView.getLayoutManager().scrollToPosition(0);
            AlignMode = !AlignMode;

        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(MyRecipeActivity.this);
            builder.setMessage("적용하시겠습니까?")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            recipeListAdapter.disableDragItem();
                            ((AppCompatImageView) v).setImageResource(R.drawable.ic_low_priority_white_24dp);
                            recipeListAdapter.notifyDataSetChanged();
                            recyclerView.getLayoutManager().scrollToPosition(0);
                            AlignMode = !AlignMode;
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    }

    private interface MyRecipeService {
        @GET("user/recipe/recipelist.php")
        Call<List<RecipeListItem>> GetRecipelistCall(@Query("User_ID") String userID, @Query("last") int last);
    }

    private class RecipeListAdapter extends BaseItemDraggableAdapter<RecipeListItem, BaseViewHolder> {
        private RequestManager requestManager;

        RecipeListAdapter(RequestManager requestManager) {
            super(R.layout.li_a_myrecipe, recipeListItems);
            this.requestManager = requestManager;
        }

        @Override
        protected void convert(BaseViewHolder helper, RecipeListItem item) {
            requestManager
                    .load(Integer.valueOf(item.getRecipeImg()))
                    .apply(RequestOptions.centerCropTransform())
                    .into((AppCompatImageView) helper.getView(R.id.li_a_myrecipe_recipeimg));

            helper.setText(R.id.li_a_myrecipe_recipetitle, item.getTitle());
            helper.setText(R.id.li_a_myrecipe_nickname, Html.fromHtml("By - <b>" + item.getNickName() + "</b>"));
            helper.setVisible(R.id.li_a_myrecipe_mine, item.getNickName().equals("나"));
            helper.setVisible(R.id.li_a_myrecipe_revise, item.getNickName().equals("나") && !AlignMode);
            helper.setVisible(R.id.li_a_myrecipe_delete, !AlignMode);
            helper.setVisible(R.id.li_a_myrecipe_touch, AlignMode);
            helper.addOnClickListener(R.id.li_a_myrecipe_revise);
            helper.addOnClickListener(R.id.li_a_myrecipe_delete);
        }
    }
}
