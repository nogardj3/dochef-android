package com.yhjoo.dochef.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.activities.HomeUserActivity;
import com.yhjoo.dochef.activities.RecipeDetailActivity;
import com.yhjoo.dochef.activities.SearchActivity;
import com.yhjoo.dochef.classes.Recipe;
import com.yhjoo.dochef.classes.UserList;
import com.yhjoo.dochef.interfaces.RetrofitServices;
import com.yhjoo.dochef.utils.BasicCallback;
import com.yhjoo.dochef.utils.DummyMaker;
import com.yhjoo.dochef.utils.RetrofitBuilder;
import com.yhjoo.dochef.view.CustomTextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Response;

public class ResultFragment extends Fragment {
    private final int VIEWHOLDER_AD = 0;
    private final int VIEWHOLDER_ITEM_RECIPE = 1;
    private final int VIEWHOLDER_ITEM_USER = 2;
    private final int VIEWHOLDER_ITEM_INGREDIENT = 3;
    private final int VIEWHOLDER_ITEM_TAG = 4;
    @BindView(R.id.result_recycler)
    RecyclerView recyclerView;
    private ResultListAdapter resultListAdapter;
    private RetrofitServices.SearchUserService searchUserService;
    private String keyword;
    private int type;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.f_result, container, false);
        ButterKnife.bind(this, view);

        type = getArguments().getInt("type");

        if (type == VIEWHOLDER_ITEM_RECIPE)
            resultListAdapter = new ResultListAdapter(new ArrayList<>(), R.layout.li_resultrecipe, Glide.with(getContext()));
        else if (type == VIEWHOLDER_ITEM_USER) {
            searchUserService = RetrofitBuilder.create(getContext(), RetrofitServices.SearchUserService.class, false);

            resultListAdapter = new ResultListAdapter(new ArrayList<>(), R.layout.li_resultuser, Glide.with(getContext()));
        } else if (type == VIEWHOLDER_ITEM_INGREDIENT)
            resultListAdapter = new ResultListAdapter(new ArrayList<>(), R.layout.li_resultingredient, Glide.with(getContext()));
        else if (type == VIEWHOLDER_ITEM_TAG)
            resultListAdapter = new ResultListAdapter(new ArrayList<>(), R.layout.li_resulttag, Glide.with(getContext()));
        else
            App.getAppInstance().showToast("ddd");

        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setAdapter(resultListAdapter);
        resultListAdapter.setEmptyView(R.layout.rv_search, (ViewGroup) recyclerView.getParent());
        resultListAdapter.setOnItemClickListener((adapter, view1, position) -> {
            switch (adapter.getItemViewType(position)) {
                case VIEWHOLDER_ITEM_RECIPE:
                case VIEWHOLDER_ITEM_INGREDIENT:
                case VIEWHOLDER_ITEM_TAG:
                    Intent intent1 = new Intent(getContext(), RecipeDetailActivity.class);
                    startActivity(intent1);
                    break;
                case VIEWHOLDER_ITEM_USER:
                    Intent intent2 = new Intent(getContext(), HomeUserActivity.class);
                    intent2.putExtra("UserID", ((UserList) ((ResultItem) adapter.getData().get(position)).getContent()).getUserID());
                    startActivity(intent2);
                    break;
            }
        });


        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (getActivity() != null && ((SearchActivity) getActivity()).getKeyword() != null) {
                if (keyword != null && !keyword.equals(((SearchActivity) getActivity()).getKeyword())) {
                    search();
                } else if (keyword == null && ((SearchActivity) getActivity()).getKeyword() != null) {
                    search();
                }
            }
        }
    }

    public void search() {
        if (((SearchActivity) getActivity()).getKeyword() != null) {
            this.keyword = ((SearchActivity) getActivity()).getKeyword();
            loadList(0);
        }
    }

    private void loadList(final int lastID) {
        resultListAdapter.setEmptyView(R.layout.rv_loading, (ViewGroup) recyclerView.getParent());
        ArrayList<Recipe> recipes = DummyMaker.make(getResources(), getResources().getInteger(R.integer.DUMMY_TYPE_RECIPIES));


        switch (type) {
            case VIEWHOLDER_ITEM_RECIPE:
                ArrayList<ResultItem> resultItems = new ArrayList<>();

                for (int i = 0; i < recipes.size(); i++) {
                    resultItems.add(new ResultItem<>(VIEWHOLDER_ITEM_RECIPE, recipes.get(i)));

                    if (i != 0 && i % 4 == 0) {
                        resultItems.add(new ResultItem<>(VIEWHOLDER_AD));
                    }
                }

                resultListAdapter.setNewData(resultItems);
                resultListAdapter.setEmptyView(R.layout.rv_empty, (ViewGroup) recyclerView.getParent());
                break;
            case VIEWHOLDER_ITEM_USER:
                searchUserService.SearchUserCall(keyword, lastID)
                        .enqueue(new BasicCallback<List<UserList>>(getContext()) {
                            @Override
                            public void onResponse(Response<List<UserList>> response) {
                                List<UserList> userList = response.body();
                                ArrayList<ResultItem> userListItem = new ArrayList<>();

                                for (int i = 0; i < userList.size(); i++) {
                                    if (i % 5 != 4)
                                        userListItem.add(new ResultItem<>(type, userList.get(i)));
                                    else {
                                        userListItem.add(new ResultItem<>(type, userList.get(i)));
                                        userListItem.add(new ResultItem<>(VIEWHOLDER_AD));
                                    }
                                }

                                if (lastID == 0)
                                    resultListAdapter.setNewData(userListItem);
                                else
                                    resultListAdapter.addData(userListItem);
                                resultListAdapter.setEmptyView(R.layout.rv_empty, (ViewGroup) recyclerView.getParent());
                            }
                        });
                break;
            case VIEWHOLDER_ITEM_INGREDIENT:
                ArrayList<ResultItem> resultItems2 = new ArrayList<>();

                for (int i = 0; i < recipes.size(); i++) {
                    resultItems2.add(new ResultItem<>(VIEWHOLDER_ITEM_INGREDIENT, recipes.get(i)));

                    if (i != 0 && i % 4 == 0) {
                        resultItems2.add(new ResultItem<>(VIEWHOLDER_AD));
                    }
                }

                resultListAdapter.setNewData(resultItems2);
                resultListAdapter.setEmptyView(R.layout.rv_empty, (ViewGroup) recyclerView.getParent());
                break;

            case VIEWHOLDER_ITEM_TAG:
                ArrayList<ResultItem> resultItems3 = new ArrayList<>();

                for (int i = 0; i < recipes.size(); i++) {
                    resultItems3.add(new ResultItem<>(VIEWHOLDER_ITEM_TAG, recipes.get(i)));

                    if (i != 0 && i % 4 == 0) {
                        resultItems3.add(new ResultItem<>(VIEWHOLDER_AD));
                    }
                }

                resultListAdapter.setNewData(resultItems3);
                resultListAdapter.setEmptyView(R.layout.rv_empty, (ViewGroup) recyclerView.getParent());
                break;
        }
    }


    private class ResultItem<T> implements MultiItemEntity {
        private final int itemType;
        private T content;

        ResultItem(int itemType, T content) {
            this.itemType = itemType;
            this.content = content;
        }

        ResultItem(int itemType) {
            this.itemType = itemType;
        }

        private T getContent() {
            return content;
        }

        @Override
        public int getItemType() {
            return itemType;
        }
    }

    private class ResultListAdapter extends BaseMultiItemQuickAdapter<ResultItem, BaseViewHolder> {
        private final RequestManager requestManager;

        ResultListAdapter(List<ResultItem> data, int layoutResId, RequestManager requestManager) {
            super(data);
            addItemType(type, layoutResId);
            addItemType(VIEWHOLDER_AD, R.layout.li_adview);
            this.requestManager = requestManager;
        }

        @Override
        protected void convert(BaseViewHolder helper, ResultItem item) {
            switch (helper.getItemViewType()) {
                case VIEWHOLDER_ITEM_RECIPE:
                    requestManager
                            .load(((Recipe) item.getContent()).getRecipeImg())
                            .apply(RequestOptions.centerCropTransform())
                            .into((AppCompatImageView) helper.getView(R.id.li_resultrecipe_recipeimg));
                    helper.setText(R.id.li_resultrecipe_title, ((Recipe) item.getContent()).getTitle());
                    helper.setText(R.id.li_resultrecipe_nickname, "By - " + ((Recipe) item.getContent()).getNickName());
                    break;

                case VIEWHOLDER_ITEM_USER:
                    requestManager
                            .load("getString(R.string.profile_image_storage_url)" + ((UserList) item.getContent()).getUserImg())
                            .apply(RequestOptions.circleCropTransform())
                            .into((AppCompatImageView) helper.getView(R.id.li_resultuser_userimg));
                    helper.setText(R.id.li_resultuser_nickname, ((UserList) item.getContent()).getNickname());
                    break;

                case VIEWHOLDER_ITEM_INGREDIENT:
                    requestManager
                            .load(((Recipe) item.getContent()).getRecipeImg())
                            .apply(RequestOptions.centerCropTransform())
                            .into((AppCompatImageView) helper.getView(R.id.li_resultingredient_recipeimg));
                    helper.setText(R.id.li_resultingredient_title, ((Recipe) item.getContent()).getTitle());
                    helper.setText(R.id.li_resultingredient_nickname, "By - " + ((Recipe) item.getContent()).getNickName());

                    ((FlexboxLayout) helper.getView(R.id.li_resultingredient_ingredients)).removeAllViews();
                    ArrayList<String> ingredients = ((Recipe) item.getContent()).getIngredients();
                    for (int i = 0; i < ingredients.size(); i++) {
                        CustomTextView ingredienttext = new CustomTextView(mContext,getResources().getColor(R.color.colorPrimary));
                        ingredienttext.setText(ingredients.get(i));
                        ((FlexboxLayout) helper.getView(R.id.li_resultingredient_ingredients)).addView(ingredienttext);
                    }
                    break;

                case VIEWHOLDER_ITEM_TAG:
                    requestManager
                            .load(((Recipe) item.getContent()).getRecipeImg())
                            .apply(RequestOptions.centerCropTransform())
                            .into((AppCompatImageView) helper.getView(R.id.li_resulttag_recipeimg));
                    helper.setText(R.id.li_resulttag_title, ((Recipe) item.getContent()).getTitle());
                    helper.setText(R.id.li_resulttag_nickname, "By - " + ((Recipe) item.getContent()).getNickName());

                    ((FlexboxLayout) helper.getView(R.id.li_resulttag_tags)).removeAllViews();
                    ArrayList<String> tags = ((Recipe) item.getContent()).getTags();
                    for (int i = 0; i < tags.size(); i++) {
                        CustomTextView tagstext = new CustomTextView(mContext,getResources().getColor(R.color.colorPrimary));
                        tagstext.setText(tags.get(i));
                        ((FlexboxLayout) helper.getView(R.id.li_resulttag_tags)).addView(tagstext);
                    }

                    break;

                case VIEWHOLDER_AD:
                    AdView mAdview = helper.getView(R.id.tempadview);
                    AdRequest adRequest = new AdRequest.Builder().build();
                    mAdview.loadAd(adRequest);
                    break;
            }
        }
    }
}
