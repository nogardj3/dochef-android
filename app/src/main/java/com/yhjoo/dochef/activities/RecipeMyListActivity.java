package com.yhjoo.dochef.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.adapter.RecipeLinearListAdapter;
import com.yhjoo.dochef.databinding.ARecipelistBinding;
import com.yhjoo.dochef.interfaces.RetrofitServices;
import com.yhjoo.dochef.model.Recipe;
import com.yhjoo.dochef.utils.BasicCallback;
import com.yhjoo.dochef.utils.DataGenerator;
import com.yhjoo.dochef.utils.RetrofitBuilder;
import com.yhjoo.dochef.utils.Utils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

public class RecipeMyListActivity extends BaseActivity {
    ARecipelistBinding binding;
    RetrofitServices.RecipeService recipeService;
    RecipeLinearListAdapter recipeLinearListAdapter;

    ArrayList<Recipe> recipeList = new ArrayList<>();
    String userID;

    /*
        TODO
        삭제하기 1. 남의거 = like 빼고 리로드 2. 내거 = 레시피 삭제
        디자인
            datetime 추가
            리뷰 수
            남의거는 수정 안되는걸로
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ARecipelistBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.recipelistToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recipeService = RetrofitBuilder.create(this, RetrofitServices.RecipeService.class);

        userID = Utils.getUserBrief(this).getUserID();

        recipeLinearListAdapter = new RecipeLinearListAdapter();
        recipeLinearListAdapter.setEmptyView(R.layout.rv_loading, (ViewGroup) binding.recipelistRecycler.getParent());
        recipeLinearListAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.recipemylist_revise) {
                Intent intent = new Intent(RecipeMyListActivity.this, RecipeMakeActivity.class)
                    .putExtra("OPERATION", RecipeMakeActivity.OPERATION.REVISE);
                startActivity(intent);
            } else if (view.getId() == R.id.recipemylist_delete) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RecipeMyListActivity.this);
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
        binding.recipelistRecycler.setLayoutManager(new LinearLayoutManager(this));
        binding.recipelistRecycler.setAdapter(recipeLinearListAdapter);

        if (App.isServerAlive())
            getRecipeList();
        else {
            recipeList = DataGenerator.make(getResources(), getResources().getInteger(R.integer.DATE_TYPE_RECIPE));
            recipeLinearListAdapter.setNewData(recipeList);
        }
    }

    void getRecipeList() {
        recipeService.getRecipeByUserID(userID,"latest")
                .enqueue(new BasicCallback<ArrayList<Recipe>>(this) {
                    @Override
                    public void onResponse(Call<ArrayList<Recipe>> call, Response<ArrayList<Recipe>> response) {
                        super.onResponse(call, response);

                        if (response.code() == 403)
                            App.getAppInstance().showToast("뭔가에러");
                        else {
                            recipeList = response.body();
                            recipeLinearListAdapter.setNewData(recipeList);
                            recipeLinearListAdapter.setEmptyView(R.layout.rv_empty, (ViewGroup) binding.recipelistRecycler.getParent());
                        }
                    }
                });
    }
}
