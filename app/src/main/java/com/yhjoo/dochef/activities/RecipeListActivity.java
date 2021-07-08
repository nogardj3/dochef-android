package com.yhjoo.dochef.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemDragListener;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.base.BaseActivity;
import com.yhjoo.dochef.classes.Recipe;
import com.yhjoo.dochef.databinding.ARecipelistBinding;

import java.util.ArrayList;

public class RecipeListActivity extends BaseActivity {
    private enum OPERATION {VIEW, ALIGN}

    ARecipelistBinding binding;

    ArrayList<Recipe> recipes = new ArrayList<>();
    RecipeListAdapter recipeListAdapter;
    ItemTouchHelper dragitemTouchHelper;

    OPERATION currentOperation = OPERATION.VIEW;

    /*
        TODO
        1. 서버 데이터 추가 및 기능 구현
        2. retrofit 구현
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ARecipelistBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.recipelistToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recipeListAdapter = new RecipeListAdapter();
        recipeListAdapter.setEmptyView(R.layout.rv_loading, (ViewGroup) binding.recipelistRecycler.getParent());
        recipeListAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.li_a_myrecipe_revise) {
                startActivity(new Intent(new Intent(RecipeListActivity.this, RecipeMakeActivity.class)));
            } else if (view.getId() == R.id.li_a_myrecipe_delete) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RecipeListActivity.this);
                builder.setMessage("삭제하시겠습니까?")
                        .setPositiveButton("확인", (dialog, which) -> {
                            adapter.getData().remove(position);
                            adapter.notifyItemRemoved(position);
                            dialog.dismiss();
                        })
                        .setNegativeButton("취소", (dialog, which) -> dialog.dismiss())
                        .show();
            }
        });

        ItemDragAndSwipeCallback itemDragAndSwipeCallback = new ItemDragAndSwipeCallback(recipeListAdapter);
        dragitemTouchHelper = new ItemTouchHelper(itemDragAndSwipeCallback);
        dragitemTouchHelper.attachToRecyclerView(binding.recipelistRecycler);
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

        binding.recipelistRecycler.setLayoutManager(new LinearLayoutManager(this));
        binding.recipelistRecycler.setAdapter(recipeListAdapter);

        binding.recipelistAlignbutton.setOnClickListener(this::toggleAlignMode);
    }

    @Override
    public void onBackPressed() {
        if (currentOperation == OPERATION.VIEW) {
            super.onBackPressed();
        } else {
            showAlignConfirm();
        }
    }

    void toggleAlignMode(View v) {
        if (currentOperation == OPERATION.VIEW) {
            recipeListAdapter.enableDragItem(dragitemTouchHelper);
            ((AppCompatImageView) v).setImageResource(R.drawable.ic_check_black_24dp);
            recipeListAdapter.notifyDataSetChanged();
            binding.recipelistRecycler.getLayoutManager().scrollToPosition(0);
            currentOperation = OPERATION.ALIGN;
        } else
            showAlignConfirm();
    }

    void showAlignConfirm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RecipeListActivity.this);
        builder.setMessage("적용하시겠습니까?")
                .setPositiveButton("확인", (dialog, which) -> {
                    recipeListAdapter.disableDragItem();
                    ((AppCompatImageView) RecipeListActivity.this.findViewById(R.id.recipelist_alignbutton)).setImageResource(R.drawable.ic_low_priority_white_24dp);
                    recipeListAdapter.notifyDataSetChanged();
                    binding.recipelistRecycler.getLayoutManager().scrollToPosition(0);
                    currentOperation = OPERATION.VIEW;
                    dialog.dismiss();
                })
                .setNegativeButton("취소", (dialog, which) -> dialog.dismiss())
                .show();
    }


    class RecipeListAdapter extends BaseItemDraggableAdapter<Recipe, BaseViewHolder> {
        RecipeListAdapter() {
            super(R.layout.li_recipelist, recipes);
        }

        @Override
        protected void convert(BaseViewHolder helper, Recipe item) {
            Glide.with(mContext)
                    .load(item.getRecipeImg())
                    .apply(RequestOptions.centerCropTransform())
                    .into((AppCompatImageView) helper.getView(R.id.li_a_myrecipe_recipeimg));

            helper.setText(R.id.li_a_myrecipe_recipetitle, item.getTitle());
            helper.setText(R.id.li_a_myrecipe_nickname, Html.fromHtml("By - <b>" + item.getNickName() + "</b>", Html.FROM_HTML_MODE_LEGACY));
            helper.setVisible(R.id.li_a_myrecipe_mine, item.getNickName().equals("나"));
            helper.setVisible(R.id.li_a_myrecipe_revise, item.getNickName().equals("나") && currentOperation == OPERATION.VIEW);
            helper.setVisible(R.id.li_a_myrecipe_delete, currentOperation == OPERATION.VIEW);
            helper.setVisible(R.id.li_a_myrecipe_touch, currentOperation == OPERATION.ALIGN);
            helper.addOnClickListener(R.id.li_a_myrecipe_revise);
            helper.addOnClickListener(R.id.li_a_myrecipe_delete);
        }
    }
}
