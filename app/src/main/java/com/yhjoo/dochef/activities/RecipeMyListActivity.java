package com.yhjoo.dochef.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.adapter.RecipeListAdapter;
import com.yhjoo.dochef.databinding.ARecipelistBinding;
import com.yhjoo.dochef.interfaces.RetrofitServices;
import com.yhjoo.dochef.model.RecipeBrief;
import com.yhjoo.dochef.model.UserBrief;
import com.yhjoo.dochef.utils.BasicCallback;
import com.yhjoo.dochef.utils.DummyMaker;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

public class RecipeMyListActivity extends BaseActivity {
    ARecipelistBinding binding;

    RecipeListAdapter recipeListAdapter;

    RetrofitServices.RecipeService recipeService;

    ArrayList<RecipeBrief> recipeList = new ArrayList<>();
    String userID = "";

    /*
        TODO
        1. 실행 해보고 수정할거 수정하기
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ARecipelistBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.recipelistToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Gson gson = new Gson();
        UserBrief userInfo = gson.fromJson(mSharedPreferences.getString(getString(R.string.SP_USERINFO), null), UserBrief.class);
        userID = userInfo.getUserID();

        recipeListAdapter = new RecipeListAdapter();
        recipeListAdapter.setEmptyView(R.layout.rv_loading, (ViewGroup) binding.recipelistRecycler.getParent());
        recipeListAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.recipemylist_revise) {
                Intent intent = new Intent(RecipeMyListActivity.this, RecipeMakeActivity.class);
                intent.putExtra("OPERATION", RecipeMakeActivity.OPERATION.REVISE);
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
        binding.recipelistRecycler.setAdapter(recipeListAdapter);

        if (App.isServerAlive()) {
            getRecipeList();
        } else {
            recipeList = DummyMaker.make(getResources(),getResources().getInteger(R.integer.DUMMY_TYPE_RECIPE_BRIEF));
            recipeListAdapter.setNewData(recipeList);
        }
    }

    void getRecipeList(){
        recipeService.getRecipeByUserID(userID)
                .enqueue(new BasicCallback<ArrayList<RecipeBrief>>(this) {
                    @Override
                    public void onResponse(Call<ArrayList<RecipeBrief>> call, Response<ArrayList<RecipeBrief>> response) {
                        super.onResponse(call, response);

                        if (response.code() == 403)
                            App.getAppInstance().showToast("뭔가에러");
                        else {
                            recipeList = response.body();
                            recipeListAdapter.setNewData(recipeList);
                            recipeListAdapter.setEmptyView(R.layout.rv_empty, (ViewGroup) binding.recipelistRecycler.getParent());
                        }
                    }
                });
    }
}
