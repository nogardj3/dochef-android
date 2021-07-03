package quvesoft.project2.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.ButterKnife;
import quvesoft.project2.Project2;
import quvesoft.project2.R;
import quvesoft.project2.activities.RecipeActivity;
import quvesoft.project2.activities.SearchActivity;
import quvesoft.project2.activities.UserHomeActivity;
import quvesoft.project2.classes.RecipeListItem;
import quvesoft.project2.classes.UserList;
import quvesoft.project2.utils.BasicCallback;
import quvesoft.project2.utils.RetrofitBuilder;
import quvesoft.project2.views.CustomTextView;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;

import static quvesoft.project2.Preferences.temprecipes;

public class ResultFragment extends Fragment {
    @BindView(R.id.result_recycler)
    RecyclerView recyclerView;

    private ResultListAdapter resultListAdapter;
    private SearchUserService searchUserService;
    private String keyword;
    private int type;

    private final int VIEWHOLDER_AD = 0;
    private final int VIEWHOLDER_ITEM_RECIPE = 1;
    private final int VIEWHOLDER_ITEM_USER = 2;
    private final int VIEWHOLDER_ITEM_INGREDIENT = 3;
    private final int VIEWHOLDER_ITEM_TAG = 4;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.f_result, container, false);
        ButterKnife.bind(this, view);

        type = getArguments().getInt("type");

        if (type == VIEWHOLDER_ITEM_RECIPE)
            resultListAdapter = new ResultListAdapter(new ArrayList<>(), R.layout.li_resultrecipe, Glide.with(this));
        else if (type == VIEWHOLDER_ITEM_USER) {
            searchUserService = RetrofitBuilder.create(getContext(), SearchUserService.class, false);

            resultListAdapter = new ResultListAdapter(new ArrayList<>(), R.layout.li_resultuser, Glide.with(this));
        } else if (type == VIEWHOLDER_ITEM_INGREDIENT)
            resultListAdapter = new ResultListAdapter(new ArrayList<>(), R.layout.li_resultingredient, Glide.with(this));
        else if (type == VIEWHOLDER_ITEM_TAG)
            resultListAdapter = new ResultListAdapter(new ArrayList<>(), R.layout.li_resulttag, Glide.with(this));
        else
            Project2.getAppInstance().showToast("ddd");

        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setAdapter(resultListAdapter);
        resultListAdapter.setEmptyView(R.layout.rv_search, (ViewGroup) recyclerView.getParent());
        resultListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                switch (adapter.getItemViewType(position)) {
                    case VIEWHOLDER_ITEM_RECIPE:
                        Intent intent1 = new Intent(getContext(), RecipeActivity.class);
                        startActivity(intent1);
                        break;
                    case VIEWHOLDER_ITEM_USER:
                        Intent intent2 = new Intent(getContext(), UserHomeActivity.class);
                        intent2.putExtra("UserID", ((UserList) ((ResultItem) adapter.getData().get(position)).getContent()).getUserID());
                        startActivity(intent2);
                        break;
                    case VIEWHOLDER_ITEM_INGREDIENT:
                        Intent intent3 = new Intent(getContext(), RecipeActivity.class);
                        startActivity(intent3);
                        break;
                    case VIEWHOLDER_ITEM_TAG:
                        Intent intent4 = new Intent(getContext(), RecipeActivity.class);
                        startActivity(intent4);
                        break;
                }
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

        switch (type) {
            case VIEWHOLDER_ITEM_RECIPE:
                List<ResultItem> aa = new ArrayList<>();
                for (int i = 0; i <= 3; i++) {
                    Random r = new Random();

                    aa.add(new ResultItem<>(VIEWHOLDER_ITEM_RECIPE, new RecipeListItem("요리" + i, "만든이" + i, "메세지" + i, 30, Integer.toString(temprecipes[r.nextInt(6)]))));
                    aa.add(new ResultItem<>(VIEWHOLDER_ITEM_RECIPE, new RecipeListItem("요리" + i, "만든이" + i, "메세지" + i, 30, Integer.toString(temprecipes[r.nextInt(6)]))));
                    aa.add(new ResultItem<>(VIEWHOLDER_ITEM_RECIPE, new RecipeListItem("요리" + i, "만든이" + i, "메세지" + i, 30, Integer.toString(temprecipes[r.nextInt(6)]))));
                    aa.add(new ResultItem<>(VIEWHOLDER_AD));
                    aa.add(new ResultItem<>(VIEWHOLDER_ITEM_RECIPE, new RecipeListItem("요리" + i, "만든이" + i, "메세지" + i, 30, Integer.toString(temprecipes[r.nextInt(6)]))));
                    aa.add(new ResultItem<>(VIEWHOLDER_ITEM_RECIPE, new RecipeListItem("요리" + i, "만든이" + i, "메세지" + i, 30, Integer.toString(temprecipes[r.nextInt(6)]))));
                    aa.add(new ResultItem<>(VIEWHOLDER_ITEM_RECIPE, new RecipeListItem("요리" + i, "만든이" + i, "메세지" + i, 30, Integer.toString(temprecipes[r.nextInt(6)]))));
                }
                resultListAdapter.setNewData(aa);
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
                ArrayList<ResultItem> bb = new ArrayList<>();
                for (int i = 0; i <= 3; i++) {
                    Random r = new Random();

                    bb.add(new ResultItem<>(VIEWHOLDER_ITEM_INGREDIENT, new RecipeListItem("요리" + i, "몽브셰" + i, "메세지" + i, 30, Integer.toString(temprecipes[r.nextInt(6)]), new ArrayList<String>() {{
                        add("재료1");
                        add("재료1");
                        add("재료1");
                    }}, new ArrayList<>())));
                    bb.add(new ResultItem<>(VIEWHOLDER_ITEM_INGREDIENT, new RecipeListItem("요리" + i, "몽브셰" + i, "메세지" + i, 30, Integer.toString(temprecipes[r.nextInt(6)]), new ArrayList<String>() {{
                        add("재료1");
                        add("재료1");
                        add("재료1");
                    }}, new ArrayList<>())));
                    bb.add(new ResultItem<>(VIEWHOLDER_ITEM_INGREDIENT, new RecipeListItem("요리" + i, "몽브셰" + i, "메세지" + i, 30, Integer.toString(temprecipes[r.nextInt(6)]), new ArrayList<String>() {{
                        add("재료1");
                        add("재료1");
                        add("재료1");
                    }}, new ArrayList<>())));
                    bb.add(new ResultItem<>(VIEWHOLDER_AD));
                    bb.add(new ResultItem<>(VIEWHOLDER_ITEM_INGREDIENT, new RecipeListItem("요리" + i, "몽브셰" + i, "메세지" + i, 30, Integer.toString(temprecipes[r.nextInt(6)]), new ArrayList<String>() {{
                        add("재료1");
                        add("재료1");
                        add("재료1");
                    }}, new ArrayList<>())));
                    bb.add(new ResultItem<>(VIEWHOLDER_ITEM_INGREDIENT, new RecipeListItem("요리" + i, "몽브셰" + i, "메세지" + i, 30, Integer.toString(temprecipes[r.nextInt(6)]), new ArrayList<String>() {{
                        add("재료1");
                        add("재료1");
                        add("재료1");
                    }}, new ArrayList<>())));
                    bb.add(new ResultItem<>(VIEWHOLDER_ITEM_INGREDIENT, new RecipeListItem("요리" + i, "몽브셰" + i, "메세지" + i, 30, Integer.toString(temprecipes[r.nextInt(6)]), new ArrayList<String>() {{
                        add("재료1");
                        add("재료1");
                        add("재료1");
                    }}, new ArrayList<>())));
                }
                resultListAdapter.setNewData(bb);
                resultListAdapter.setEmptyView(R.layout.rv_empty, (ViewGroup) recyclerView.getParent());
                break;

            case VIEWHOLDER_ITEM_TAG:
                ArrayList<ResultItem> cc = new ArrayList<>();

                for (int i = 0; i <= 3; i++) {
                    Random r = new Random();

                    cc.add(new ResultItem<>(VIEWHOLDER_ITEM_TAG, new RecipeListItem("요리" + i, "몽브셰" + i, "메세지" + i, 30, Integer.toString(temprecipes[r.nextInt(6)]), new ArrayList<>(), new ArrayList<String>() {{
                        add("태그1");
                        add("태그1");
                    }})));
                    cc.add(new ResultItem<>(VIEWHOLDER_ITEM_TAG, new RecipeListItem("요리" + i, "몽브셰" + i, "메세지" + i, 30, Integer.toString(temprecipes[r.nextInt(6)]), new ArrayList<>(), new ArrayList<String>() {{
                        add("태그1");
                        add("태그1");
                    }})));
                    cc.add(new ResultItem<>(VIEWHOLDER_ITEM_TAG, new RecipeListItem("요리" + i, "몽브셰" + i, "메세지" + i, 30, Integer.toString(temprecipes[r.nextInt(6)]), new ArrayList<>(), new ArrayList<String>() {{
                        add("태그1");
                        add("태그1");
                    }})));
                    cc.add(new ResultItem<>(VIEWHOLDER_AD));
                    cc.add(new ResultItem<>(VIEWHOLDER_ITEM_TAG, new RecipeListItem("요리" + i, "몽브셰" + i, "메세지" + i, 30, Integer.toString(temprecipes[r.nextInt(6)]), new ArrayList<>(), new ArrayList<String>() {{
                        add("태그1");
                        add("태그1");
                    }})));
                    cc.add(new ResultItem<>(VIEWHOLDER_ITEM_TAG, new RecipeListItem("요리" + i, "몽브셰" + i, "메세지" + i, 30, Integer.toString(temprecipes[r.nextInt(6)]), new ArrayList<>(), new ArrayList<String>() {{
                        add("태그1");
                        add("태그1");
                    }})));
                    cc.add(new ResultItem<>(VIEWHOLDER_ITEM_TAG, new RecipeListItem("요리" + i, "몽브셰" + i, "메세지" + i, 30, Integer.toString(temprecipes[r.nextInt(6)]), new ArrayList<>(), new ArrayList<String>() {{
                        add("태그1");
                        add("태그1");
                    }})));
                }
                resultListAdapter.setNewData(cc);
                resultListAdapter.setEmptyView(R.layout.rv_empty, (ViewGroup) recyclerView.getParent());
                break;
        }
    }

    private interface SearchUserService {
        @GET("search/user.php")
        Call<List<UserList>> SearchUserCall(@Query("keyword") String keyword, @Query("last") int last);
    }

    private class ResultItem<T> implements MultiItemEntity {
        private int itemType;
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
        private RequestManager requestManager;

        ResultListAdapter(List<ResultItem> data, int layoutResId, RequestManager requestManager) {
            super(data);
            addItemType(type, layoutResId);
            addItemType(VIEWHOLDER_AD, R.layout.li_tempadview);
            this.requestManager = requestManager;
        }

        @Override
        protected void convert(BaseViewHolder helper, ResultItem item) {
            switch (helper.getItemViewType()) {
                case VIEWHOLDER_ITEM_RECIPE:
                    requestManager
                            .load(Integer.valueOf(((RecipeListItem) item.getContent()).getRecipeImg()))
                            .apply(RequestOptions.centerCropTransform())
                            .into((AppCompatImageView) helper.getView(R.id.li_resultrecipe_recipeimg));
                    helper.setText(R.id.li_resultrecipe_title, ((RecipeListItem) item.getContent()).getTitle());
                    helper.setText(R.id.li_resultrecipe_nickname, "By - " + ((RecipeListItem) item.getContent()).getNickName());
                    break;

                case VIEWHOLDER_ITEM_USER:
                    requestManager
                            .load("https://s3.ap-northeast-2.amazonaws.com/quvechefbucket/profile/" + ((UserList) item.getContent()).getUserImg())
                            .apply(RequestOptions.circleCropTransform())
                            .into((AppCompatImageView) helper.getView(R.id.li_resultuser_userimg));
                    helper.setText(R.id.li_resultuser_nickname, ((UserList) item.getContent()).getNickname());
                    break;

                case VIEWHOLDER_ITEM_INGREDIENT:
                    requestManager
                            .load(Integer.valueOf(((RecipeListItem) item.getContent()).getRecipeImg()))
                            .apply(RequestOptions.centerCropTransform())
                            .into((AppCompatImageView) helper.getView(R.id.li_resultingredient_recipeimg));
                    helper.setText(R.id.li_resultingredient_title, ((RecipeListItem) item.getContent()).getTitle());
                    helper.setText(R.id.li_resultingredient_nickname, "By - " + ((RecipeListItem) item.getContent()).getNickName());

                    ((FlexboxLayout) helper.getView(R.id.li_resultingredient_ingredients)).removeAllViews();
                    ArrayList<String> ingredients = ((RecipeListItem) item.getContent()).getIngredients();
                    for (int i = 0; i < ingredients.size(); i++) {
                        CustomTextView ingredienttext = new CustomTextView(mContext);
                        ingredienttext.setText(ingredients.get(i));
                        ingredienttext.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
                        ingredienttext.setTypeface(ingredienttext.getTypeface(), Typeface.BOLD);
                        ((FlexboxLayout) helper.getView(R.id.li_resultingredient_ingredients)).addView(ingredienttext);
                    }
                    break;

                case VIEWHOLDER_ITEM_TAG:
                    requestManager
                            .load(Integer.valueOf(((RecipeListItem) item.getContent()).getRecipeImg()))
                            .apply(RequestOptions.centerCropTransform())
                            .into((AppCompatImageView) helper.getView(R.id.li_resulttag_recipeimg));
                    helper.setText(R.id.li_resulttag_title, ((RecipeListItem) item.getContent()).getTitle());
                    helper.setText(R.id.li_resulttag_nickname, "By - " + ((RecipeListItem) item.getContent()).getNickName());

                    ((FlexboxLayout) helper.getView(R.id.li_resulttag_tags)).removeAllViews();
                    ArrayList<String> tags = ((RecipeListItem) item.getContent()).getTags();
                    for (int i = 0; i < tags.size(); i++) {
                        CustomTextView tagstext = new CustomTextView(mContext);
                        tagstext.setText(tags.get(i));
                        tagstext.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
                        tagstext.setTypeface(tagstext.getTypeface(), Typeface.BOLD);
                        ((FlexboxLayout) helper.getView(R.id.li_resulttag_tags)).addView(tagstext);
                    }

                    break;

                case VIEWHOLDER_AD:
                    MobileAds.initialize(mContext.getApplicationContext(), getString(R.string.admob_app_id));
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
