package com.yhjoo.dochef.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.android.flexbox.FlexboxLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.yhjoo.dochef.Preferences;
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.activities.CommentActivity;
import com.yhjoo.dochef.activities.MyHomeActivity;
import com.yhjoo.dochef.activities.PostActivity;
import com.yhjoo.dochef.activities.RevisePostActivity;
import com.yhjoo.dochef.activities.UserHomeActivity;
import com.yhjoo.dochef.classes.Post;
import com.yhjoo.dochef.classes.PostComment;
import com.yhjoo.dochef.utils.BasicCallback;
import com.yhjoo.dochef.views.CustomLoadMoreView;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class TimeLineFragment extends Fragment implements BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.timeline_swipe)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.timeline_recycler)
    RecyclerView recyclerView;

    private PostListAdapter postListAdapter;
    private TimeLineService timeLineService;
    private List<Post> postList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.f_timeline, container, false);
        ButterKnife.bind(this, view);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        postListAdapter = new PostListAdapter(Glide.with(getContext()));
        postListAdapter.setOnLoadMoreListener(this, recyclerView);
        postListAdapter.setLoadMoreView(new CustomLoadMoreView());
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setAdapter(postListAdapter);
        postListAdapter.setEmptyView(R.layout.rv_loading, (ViewGroup) recyclerView.getParent());
        postListAdapter.setOnItemClickListener((adapter, view1, position) -> {
            Intent intent = new Intent(TimeLineFragment.this.getContext(), PostActivity.class);
            intent.putExtra("post", (Post) adapter.getData().get(position));
            startActivity(intent);
        });
        postListAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                switch (view.getId()) {
                    case R.id.timeline_like:
                        App.getAppInstance().showToast("좋아요");
                        break;
                    case R.id.timeline_comment:
                        startActivity(new Intent(TimeLineFragment.this.getContext(), CommentActivity.class));
                        break;

                    case R.id.timeline_other:
                        PopupMenu popup = new PopupMenu(TimeLineFragment.this.getContext(), view);
                        //if(ismaster)
                        TimeLineFragment.this.getActivity().getMenuInflater().inflate(R.menu.menu_post_master, popup.getMenu());
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
//                                case R.id.menu_post_normal_report:
//                                    break;

                                    case R.id.menu_post_master_revise:
                                        Intent intent = new Intent(TimeLineFragment.this.getContext(), RevisePostActivity.class);
                                        intent.putExtra("postid", ((Post) baseQuickAdapter.getData().get(i)).getPostID())
                                                .putExtra("contents", ((Post) baseQuickAdapter.getData().get(i)).getContents())
                                                .putExtra("postimg", "https://s3.ap-northeast-2.amazonaws.com/quvechefbucket/postImage/" + ((Post) baseQuickAdapter.getData().get(i)).getPostImg());
                                        startActivity(intent);

                                        break;
                                    case R.id.menu_post_master_delete:
                                        AlertDialog.Builder builder = new AlertDialog.Builder(TimeLineFragment.this.getContext());
                                        builder.setMessage("삭제하시겠습니까?")
                                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        baseQuickAdapter.getData().remove(i);
                                                        baseQuickAdapter.notifyItemRemoved(i);
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
                                        break;
                                }
                                return false;
                            }
                        });
                        popup.show();
                        break;

                    case R.id.timeline_user_layout:
                        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                        try {
                            if (mSharedPreferences.getBoolean(Preferences.SHAREDPREFERENCE_AUTOLOGIN, false) &&
                                    ((Post) baseQuickAdapter.getData().get(i)).getUserID().equals(new JSONObject(mSharedPreferences.getString(Preferences.SHAREDPREFERENCE_USERINFO, null)).getString("USER_ID"))) {

                                Intent intent = new Intent(getContext(), MyHomeActivity.class);
                                startActivity(intent);

                            } else {
                                Intent intent = new Intent(getContext(), UserHomeActivity.class);
                                intent.putExtra("UserID", ((Post) baseQuickAdapter.getData().get(i)).getUserID());
                                startActivity(intent);
                            }
                        } catch (JSONException e) {
                            Intent intent = new Intent(getContext(), UserHomeActivity.class);
                            intent.putExtra("UserID", ((Post) baseQuickAdapter.getData().get(i)).getUserID());
                            startActivity(intent);
                        }
                        break;
                }
            }
        });

        timeLineService = new Retrofit.Builder()
                .baseUrl(getString(R.string.server_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(TimeLineService.class);
        postListAdapter.setEnableLoadMore(true);

        return view;
    }

    @Override
    public void onLoadMoreRequested() {
        swipeRefreshLayout.setEnabled(false);
        postListAdapter.loadMoreEnd(true);
        swipeRefreshLayout.setEnabled(true);
    }

    @Override
    public void onRefresh() {
        postListAdapter.setEnableLoadMore(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                postListAdapter.setNewData(postList);
                swipeRefreshLayout.setRefreshing(false);
                postListAdapter.setEnableLoadMore(true);
            }
        }, 1000);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (postListAdapter.getData().size() == 0) {
                timeLineService.GetPostCall(0)
                        .enqueue(new BasicCallback<ArrayList<Post>>(getContext()) {
                            @Override
                            public void onResponse(Response<ArrayList<Post>> response) {
                                postList.clear();

                                postList = response.body();
                                postListAdapter.setNewData(postList);
                                postListAdapter.setEmptyView(R.layout.rv_empty, (ViewGroup) recyclerView.getParent());
                            }

                            @Override

                            public void onFailure() {
                            }
                        });
            }
        }
    }

    private interface TimeLineService {
        @GET("post/timeline.php")
        Call<ArrayList<Post>> GetPostCall(@Query("last") int last);
    }

    private class PostListAdapter extends BaseQuickAdapter<Post, BaseViewHolder> {
        private final RequestManager requestManager;

        PostListAdapter(RequestManager requestManager) {
            super(R.layout.li_timeline);
            this.requestManager = requestManager;
        }

        @Override
        protected void convert(BaseViewHolder helper, Post item) {
            requestManager
                    .load("https://s3.ap-northeast-2.amazonaws.com/quvechefbucket/profile/" + item.getUserImg())
                    .apply(RequestOptions.placeholderOf(R.drawable.tempimg_profile1).centerCrop())
                    .into((AppCompatImageView) helper.getView(R.id.timeline_userimg));

            requestManager
                    .load("https://s3.ap-northeast-2.amazonaws.com/quvechefbucket/postImage/" + item.getPostImg())
                    .apply(RequestOptions.centerCropTransform())
                    .into((AppCompatImageView) helper.itemView.findViewById(R.id.timeline_postimg));

            helper.setText(R.id.timeline_nickname, item.getNickname());
            helper.setText(R.id.timeline_likecount, "0");
            helper.setText(R.id.timeline_contents, " " + item.getContents());
            helper.setText(R.id.timeline_time, "1일전");
            helper.addOnClickListener(R.id.timeline_like);
            helper.addOnClickListener(R.id.timeline_comment);
            helper.addOnClickListener(R.id.timeline_other);
            helper.addOnClickListener(R.id.timeline_user_layout);

            ((FlexboxLayout) helper.getView(R.id.timeline_tags)).removeAllViews();
            String[] tags = {"tag1", "tag2", "tag3", "tag4"};

            for (int i = 0; i < tags.length; i++) {
                AppCompatTextView textView = new AppCompatTextView(TimeLineFragment.this.getContext());
                textView.setText("#" + tags[i] + " ");
                textView.setTextColor(getResources().getColor(R.color.colorPrimary));

                ((FlexboxLayout) helper.getView(R.id.timeline_tags)).addView(textView);
            }

            ((FlexboxLayout) helper.getView(R.id.timeline_commentdetail)).removeAllViews();
            List<PostComment> aa = new ArrayList<>();
            PostComment bb = new PostComment();
            bb.setNickName("유저1");
            bb.setContents("댓글1\n댓글1\n댓글1\n댓글1");
            bb.setDate(0);

            aa.add(bb);
            aa.add(bb);

            if (aa.size() != 0) {
                helper.getView(R.id.timeline_comment_layout).setVisibility(View.VISIBLE);
                for (int i = 0; i < aa.size(); i++) {
                    LinearLayout motherview = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.li_comment_brief, null);
                    AppCompatTextView view1 = ((AppCompatTextView) motherview.findViewById(R.id.li_comment_brief_nickname));
                    view1.setText(aa.get(i).getNickName());
                    AppCompatTextView view2 = ((AppCompatTextView) motherview.findViewById(R.id.li_comment_brief_contents));
                    view2.setText(aa.get(i).getContents());
                    AppCompatTextView view3 = ((AppCompatTextView) motherview.findViewById(R.id.li_comment_brief_date));
                    view3.setText("1일전");

                    ((FlexboxLayout) helper.itemView.findViewById(R.id.timeline_commentdetail)).addView(motherview);
                }
            } else {
                helper.getView(R.id.timeline_comment_layout).setVisibility(View.GONE);
            }
        }
    }
}