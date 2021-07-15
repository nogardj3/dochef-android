package com.yhjoo.dochef.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.activities.HomeActivity;
import com.yhjoo.dochef.activities.PostDetailActivity;
import com.yhjoo.dochef.adapter.PostListAdapter;
import com.yhjoo.dochef.databinding.FMainTimelineBinding;
import com.yhjoo.dochef.interfaces.RetrofitServices;
import com.yhjoo.dochef.model.Post;
import com.yhjoo.dochef.model.UserBrief;
import com.yhjoo.dochef.utils.BasicCallback;
import com.yhjoo.dochef.utils.DataGenerator;
import com.yhjoo.dochef.utils.RetrofitBuilder;
import com.yhjoo.dochef.utils.Utils;

import java.util.ArrayList;

import retrofit2.Response;

public class MainTimelineFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    FMainTimelineBinding binding;
    RetrofitServices.PostService postService;
    PostListAdapter postListAdapter;
    ArrayList<Post> postList;

    /*
        TODO
        postDetail = 수정 O, 댓글 많이, 댓글 작성 가능
        timeline   = 수정 X, 댓글 하나, 댓글 작성 불가

        swipe refresh 왜 안됨
    */

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FMainTimelineBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        postService =
                RetrofitBuilder.create(getContext(), RetrofitServices.PostService.class);

        postListAdapter = new PostListAdapter();
        binding.timelineSwipe.setOnRefreshListener(this);
        binding.timelineSwipe.setColorSchemeColors(getResources().getColor(R.color.colorPrimary, null));
        postListAdapter.setEmptyView(R.layout.rv_loading, (ViewGroup) binding.timelineRecycler.getParent());
        postListAdapter.setOnItemClickListener((adapter, view1, position) -> {
            Intent intent = new Intent(MainTimelineFragment.this.getContext(), PostDetailActivity.class);
            intent.putExtra("postID", ((Post) adapter.getData().get(position)).getPostID());
            startActivity(intent);
        });
        postListAdapter.setOnItemChildClickListener((baseQuickAdapter, view12, i) -> {
            switch (view12.getId()) {
                case R.id.timeline_user_group:
                    SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                    Gson gson = new Gson();
                    UserBrief userInfo = gson.fromJson(mSharedPreferences.getString(getString(R.string.SP_USERINFO), null), UserBrief.class);
                    String active_userid = userInfo.getUserID();

                    String item_userid = ((Post) baseQuickAdapter.getData().get(i)).getUserID();

                    Intent intent = new Intent(getContext(), HomeActivity.class);
                    if (item_userid.equals(active_userid))
                        intent.putExtra("MODE", HomeActivity.MODE.MY);
                    else {
                        intent.putExtra("MODE", HomeActivity.MODE.USER);
                        intent.putExtra("userID", ((Post) baseQuickAdapter.getData().get(i)).getUserID());
                    }
                    startActivity(intent);
                    break;

                case R.id.timeline_comment_group:
                case R.id.timeline_contents:
                case R.id.timeline_postimg:
                    Intent intent2 = new Intent(MainTimelineFragment.this.getContext(), PostDetailActivity.class);
                    intent2.putExtra("postID", ((Post) baseQuickAdapter.getData().get(i)).getPostID());
                    startActivity(intent2);
                    break;
            }
        });
        binding.timelineRecycler.setLayoutManager(new LinearLayoutManager(this.getContext()));
        binding.timelineRecycler.setAdapter(postListAdapter);

        return view;
    }

    @Override
    public void onRefresh() {
        binding.timelineSwipe.setRefreshing(true);
        binding.timelineSwipe.setEnabled(false);
        getPostList();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (App.isServerAlive()) {
            getPostList();
        } else {
            postList = DataGenerator.make(getResources(), getResources().getInteger(R.integer.DUMMY_TYPE_POST));
            postListAdapter.setNewData(postList);
            postListAdapter.notifyDataSetChanged();
        }
    }

    void getPostList() {
        postService
                .getPostList()
                .enqueue(new BasicCallback<ArrayList<Post>>(getContext()) {
                    @Override
                    public void onResponse(Response<ArrayList<Post>> response) {
                        if (response.code() == 500) {
                            App.getAppInstance().showToast("post detail 가져오기 실패");
                        } else {
                            postList = response.body();
                            postListAdapter.setNewData(response.body());
                            postListAdapter.notifyDataSetChanged();
                            postListAdapter.setEmptyView(R.layout.rv_empty, (ViewGroup) binding.timelineSwipe.getParent());

                            new Handler().postDelayed(() -> binding.timelineSwipe.setRefreshing(false), 1000);
                        }
                    }
                });
    }
}