package com.yhjoo.dochef.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.activities.HomeActivity;
import com.yhjoo.dochef.activities.PostDetailActivity;
import com.yhjoo.dochef.adapter.PostListAdapter;
import com.yhjoo.dochef.databinding.FMainTimelineBinding;
import com.yhjoo.dochef.interfaces.RetrofitServices;
import com.yhjoo.dochef.model.Post;
import com.yhjoo.dochef.utils.BasicCallback;
import com.yhjoo.dochef.utils.DataGenerator;
import com.yhjoo.dochef.utils.RetrofitBuilder;

import java.util.ArrayList;

import retrofit2.Response;

public class MainTimelineFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    FMainTimelineBinding binding;
    RetrofitServices.PostService postService;
    PostListAdapter postListAdapter;

    ArrayList<Post> postList;

    /*
        TODO
    */

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FMainTimelineBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        postService =
                RetrofitBuilder.create(getContext(), RetrofitServices.PostService.class);

        binding.timelineSwipe.setOnRefreshListener(this);
        binding.timelineSwipe.setColorSchemeColors(getResources().getColor(R.color.colorPrimary, null));
        postListAdapter = new PostListAdapter();
        postListAdapter.setEmptyView(R.layout.rv_loading, (ViewGroup) binding.timelineRecycler.getParent());
        postListAdapter.setOnItemClickListener((adapter, view1, position) -> {
            Intent intent = new Intent(MainTimelineFragment.this.getContext(), PostDetailActivity.class)
                .putExtra("postID", ((Post) adapter.getData().get(position)).getPostID());
            startActivity(intent);
        });
        postListAdapter.setOnItemChildClickListener((baseQuickAdapter, view12, i) -> {
            switch (view12.getId()) {
                case R.id.timeline_userimg:
                case R.id.timeline_nickname:
                    Intent intent = new Intent(getContext(), HomeActivity.class)
                        .putExtra("userID", ((Post) baseQuickAdapter.getData().get(i)).getUserID());
                    startActivity(intent);
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
        if (App.isServerAlive())
            getPostList();
        else {
            postList = DataGenerator.make(getResources(), getResources().getInteger(R.integer.DATA_TYPE_POST));
            postListAdapter.setNewData(postList);
            postListAdapter.notifyDataSetChanged();
        }
    }

    void getPostList() {
        postService
                .getPostList()
                .enqueue(new BasicCallback<ArrayList<Post>>(this.getContext()) {
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