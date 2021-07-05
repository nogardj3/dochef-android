package com.yhjoo.dochef.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.android.flexbox.FlexboxLayout;
import com.google.gson.annotations.SerializedName;
import com.viewpagerindicator.CirclePageIndicator;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.base.BaseActivity;
import com.yhjoo.dochef.classes.Comment;
import com.yhjoo.dochef.classes.RecipeListItem;
import com.yhjoo.dochef.classes.Review;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

import static com.yhjoo.dochef.Preferences.tempprofile;
import static com.yhjoo.dochef.Preferences.temprecipes;

public class RecipeActivity extends BaseActivity {
    private RecipeOverview recipeOverview;
    private final int RecipeID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_recipe);
        ButterKnife.bind(this);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.server_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        OverViewService overViewService = retrofit.create(OverViewService.class);

        overViewService.LoadOverViewCall(RecipeID).enqueue(new Callback<RecipeOverview>() {
            @Override
            public void onResponse(Call<RecipeOverview> call, Response<RecipeOverview> response1) {
                recipeOverview = response1.body();
                overViewService.LoadCommentCall(RecipeID)
                        .enqueue(new Callback<ArrayList<Comment>>() {
                            @Override
                            public void onResponse(Call<ArrayList<Comment>> call, Response<ArrayList<Comment>> response) {
                                try {
                                    setheaderview();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(Call<ArrayList<Comment>> call, Throwable t) {

                            }
                        });
            }

            @Override
            public void onFailure(Call<RecipeOverview> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    void setheaderview() {
        try {
            ((ViewPager) findViewById(R.id.recipe_recipeimgs)).setAdapter(new ImagePagerAdapter(RecipeActivity.this,
                    new ArrayList<Integer>(Arrays.asList(R.drawable.tempimg_playrecipestart, R.drawable.tempimg_playrecipe1, R.drawable.tempimg_playrecipe2, R.drawable.tempimg_playrecipe3, R.drawable.tempimg_playrecipe4, R.drawable.tempimg_playrecipefinish)), Glide.with(this)));
            ((CirclePageIndicator) findViewById(R.id.recipe_recipeimgs_indicator)).setViewPager(((ViewPager) findViewById(R.id.recipe_recipeimgs)));

            ((AppCompatTextView) findViewById(R.id.recipe_recipetitle)).setText(recipeOverview.getTitle());

            Glide.with(this)
                    .load("https://s3.ap-northeast-2.amazonaws.com/quvechefbucket/profile/" + recipeOverview.getProducerID())
                    .apply(RequestOptions.circleCropTransform())
                    .into((AppCompatImageView) findViewById(R.id.recipe_userimg));

            ((AppCompatTextView) findViewById(R.id.recipe_nickname)).setText(recipeOverview.getProducerName());

            ((AppCompatTextView) findViewById(R.id.recipe_explain)).setText(recipeOverview.getSubstance());

            JSONArray tagsArray = new JSONArray(recipeOverview.getTag());

            for (int i = 0; i < tagsArray.length(); i++) {
                AppCompatTextView textView = new AppCompatTextView(this);
                textView.setText(tagsArray.getString(i));
                textView.setTextColor(getResources().getColor(R.color.colorPrimary));

                ((FlexboxLayout) findViewById(R.id.recipe_tags)).addView(textView);
            }

            findViewById(R.id.recipe_startrecipe).setOnClickListener((v) -> startActivity(new Intent(this, PlayRecipeActivity.class)));
//            findViewById(R.id.recipe_startrecipe).setOnClickListener((v) -> startActivity(new Intent(this, TempActivity.class)));

            ((FlexboxLayout) findViewById(R.id.recipe_ingredients)).removeAllViews();
            ArrayList<JSONObject> ingredientsItems = new ArrayList<>();
            JSONArray aa = new JSONArray(recipeOverview.getIngredients());
            for (int i = 0; i < aa.length(); i++)
                ingredientsItems.add(aa.getJSONObject(i));

            for (int i = 0; i < aa.length(); i++) {
                @SuppressLint("InflateParams") ViewGroup motherview = (ViewGroup) getLayoutInflater().inflate(R.layout.li_ingredient, null);
                AppCompatTextView view1 = ((AppCompatTextView) motherview.findViewById(R.id.li_ingredient_product));
                view1.setText(aa.getJSONObject(i).getString("name"));
                AppCompatTextView view2 = ((AppCompatTextView) motherview.findViewById(R.id.li_ingredient_quantity));
                view2.setText(aa.getJSONObject(i).getString("amount"));

                ((FlexboxLayout) findViewById(R.id.recipe_ingredients)).addView(motherview);
            }

            ArrayList<Review> bb = new ArrayList<>();
            Random r = new Random();
            bb.add(new Review(Integer.toString(temprecipes[r.nextInt(6)]), "userid", Integer.toString(tempprofile[r.nextInt(6)]), "유저0", "내용", 0, 3));
            bb.add(new Review(Integer.toString(temprecipes[r.nextInt(6)]), "userid", Integer.toString(tempprofile[r.nextInt(6)]), "유저1", "내용", 0, 5));

            findViewById(R.id.recipe_review_more).setVisibility(bb.size() >= 2 ? View.VISIBLE : View.GONE);
            findViewById(R.id.recipe_review_more).setOnClickListener((v) -> startActivity(new Intent(this, ReviewActivity.class)));

            ReviewListAdapter reviewListAdapter = new ReviewListAdapter(bb, Glide.with(this));
            reviewListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                    startActivity(new Intent(RecipeActivity.this, ReviewActivity.class));
                }
            });
            ((RecyclerView) findViewById(R.id.recipe_review_recycler)).setLayoutManager(new LinearLayoutManager(this));
            ((RecyclerView) findViewById(R.id.recipe_review_recycler)).setAdapter(reviewListAdapter);


            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recipe_theme_recycler);

            ArrayList<RecipeListItem> recipeListItems = new ArrayList<>();
            for (int i = 0; i < 6; i++) {
                Random r2 = new Random();
                recipeListItems.add(new RecipeListItem("추천" + i, "만든이" + i, "메세지" + i, 20, Integer.toString(temprecipes[r2.nextInt(6)]), new ArrayList<>(), new ArrayList<>()));
            }

            recyclerView.setLayoutManager(new LinearLayoutManager(RecipeActivity.this, LinearLayoutManager.HORIZONTAL, false));
            RecommendAdapter recommendAdapter = new RecommendAdapter(recipeListItems, Glide.with(this));
            recyclerView.setAdapter(recommendAdapter);
            recommendAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    startActivity(new Intent(RecipeActivity.this, ThemeActivity.class));
                }
            });

            findViewById(R.id.recipe_theme_more).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(RecipeActivity.this, ThemeActivity.class));
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private interface OverViewService {
        @FormUrlEncoded
        @POST("recipe/overview.php")
        Call<RecipeOverview> LoadOverViewCall(@Field("recipeID") int id);

        @FormUrlEncoded
        @POST("recipe/loadComment.php")
        Call<ArrayList<Comment>> LoadCommentCall(@Field("recipeID") int id);
    }

    private class RecipeOverview {
        @SerializedName("RECIPE_ID")
        private int recipeID;
        @SerializedName("USER_ID")
        private String producerID;
        @SerializedName("NICKNAME")
        private String producerName;
        @SerializedName("TITLE")
        private String title;
        @SerializedName("SUBSTANCE")
        private String substance;
        @SerializedName("THUMBNAIL")
        private String thumbnail;
        @SerializedName("INGREDIENTS")
        private String ingredients;
        @SerializedName("TIME")
        private long Time;
        @SerializedName("TAG")
        private String Tag;
        @SerializedName("FILE")
        private String file;

        public String getProducerID() {
            return producerID;
        }

        public String getProducerName() {
            return producerName;
        }

        public int getRecipeID() {
            return recipeID;
        }

        public long getTime() {
            return Time;
        }

        public String getFile() {
            return file;
        }

        public String getIngredients() {
            return ingredients;
        }

        public String getSubstance() {
            return substance;
        }

        public String getTag() {
            return Tag;
        }

        public String getThumbnail() {
            return thumbnail;
        }

        public String getTitle() {
            return title;
        }
    }

    public class ImagePagerAdapter extends PagerAdapter {
        private final Context mContext;
        private final ArrayList<Integer> imgids;
        private final RequestManager requestManager;

        public ImagePagerAdapter(Context context, ArrayList<Integer> imgids, RequestManager requestManager) {
            this.mContext = context;
            this.imgids = imgids;
            this.requestManager = requestManager;
        }

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            AppCompatImageView aa = new AppCompatImageView(mContext);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            aa.setLayoutParams(lp);

            requestManager.load(imgids.get(position))
                    .apply(RequestOptions.centerCropTransform())
                    .into(aa);

            collection.addView(aa);

            return aa;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }

        @Override
        public int getCount() {
            return imgids.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    private class RecommendAdapter extends BaseQuickAdapter<RecipeListItem, BaseViewHolder> {
        private final RequestManager requestManager;

        RecommendAdapter(ArrayList<RecipeListItem> recipeListItem, RequestManager requestManager) {
            super(R.layout.li_recommend, recipeListItem);
            this.requestManager = requestManager;
        }

        @Override
        protected void convert(BaseViewHolder helper, RecipeListItem item) {
            requestManager.load(Integer.valueOf(item.getRecipeImg()))
                    .apply(RequestOptions.centerCropTransform())
                    .into((AppCompatImageView) helper.getView(R.id.li_recommend_recipeimg));
            helper.setText(R.id.li_recommend_title, item.getTitle());
            helper.setText(R.id.li_recommend_nickname, "By - " + item.getNickName());
        }
    }

    private class ReviewListAdapter extends BaseQuickAdapter<Review, BaseViewHolder> {
        private final RequestManager requestManager;

        ReviewListAdapter(@Nullable ArrayList<Review> data, RequestManager requestManager) {
            super(R.layout.li_reviewbrief, data);
            this.requestManager = requestManager;
        }

        @Override
        protected void convert(BaseViewHolder helper, Review item) {
            requestManager
                    .load(Integer.valueOf(item.getImageURL()))
                    .apply(RequestOptions.centerCropTransform())
                    .into((AppCompatImageView) helper.getView(R.id.reviewbrief_recipeimg));

            requestManager
                    .load(Integer.valueOf(item.getUserImg()))
                    .apply(RequestOptions.circleCropTransform())
                    .into((AppCompatImageView) helper.getView(R.id.reviewbrief_userimg));

            helper.setRating(R.id.reviewbrief_rating, item.getRating());
            helper.setText(R.id.reviewbrief_nickname, item.getNickname());
            helper.setText(R.id.reviewbrief_contents, item.getContents());
            helper.setText(R.id.reviewbrief_date, "1일전");
        }
    }
}
