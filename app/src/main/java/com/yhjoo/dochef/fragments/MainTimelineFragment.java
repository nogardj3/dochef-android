package com.yhjoo.dochef.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
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
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.activities.CommentActivity;
import com.yhjoo.dochef.activities.HomeActivity;
import com.yhjoo.dochef.activities.PostDetailActivity;
import com.yhjoo.dochef.activities.PostWriteActivity;
import com.yhjoo.dochef.classes.Post;
import com.yhjoo.dochef.classes.PostComment;
import com.yhjoo.dochef.interfaces.RetrofitServices;
import com.yhjoo.dochef.utils.BasicCallback;
import com.yhjoo.dochef.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainTimelineFragment extends Fragment implements BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.timeline_swipe)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.timeline_recycler)
    RecyclerView recyclerView;

    private PostListAdapter postListAdapter;
    private RetrofitServices.TimeLineService timeLineService;

    private List<Post> postList = new ArrayList<>();

    /*
        TODO
        1. 타임 컨버터 적용 -> 시간 적용
    */

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.f_main_timeline, container, false);
        ButterKnife.bind(this, view);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary,null));
        postListAdapter = new PostListAdapter(Glide.with(getContext()));
        postListAdapter.setOnLoadMoreListener(this, recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setAdapter(postListAdapter);
        postListAdapter.setEmptyView(R.layout.rv_loading, (ViewGroup) recyclerView.getParent());
        postListAdapter.setOnItemClickListener((adapter, view1, position) -> {
            Intent intent = new Intent(MainTimelineFragment.this.getContext(), PostDetailActivity.class);
            intent.putExtra("post", (Post) adapter.getData().get(position));
            startActivity(intent);
        });
        postListAdapter.setOnItemChildClickListener((baseQuickAdapter, view12, i) -> {
            switch (view12.getId()) {
                case R.id.timeline_like:
                    App.getAppInstance().showToast("좋아요");
                    break;
                case R.id.timeline_comment:
                    startActivity(new Intent(MainTimelineFragment.this.getContext(), CommentActivity.class));
                    break;

                case R.id.timeline_other:
                    PopupMenu popup = new PopupMenu(MainTimelineFragment.this.getContext(), view12);
                    //if(ismaster)
                    MainTimelineFragment.this.getActivity().getMenuInflater().inflate(R.menu.menu_post_owner, popup.getMenu());
                    popup.setOnMenuItemClickListener(item -> {
                        switch (item.getItemId()) {
                            case R.id.menu_post_owner_revise:
                                Intent intent = new Intent(MainTimelineFragment.this.getContext(), PostWriteActivity.class);
                                intent.putExtra("postid", ((Post) baseQuickAdapter.getData().get(i)).getPostID())
                                        .putExtra("contents", ((Post) baseQuickAdapter.getData().get(i)).getContents())
                                        .putExtra("postimg", getString(R.string.storage_image_url_post) + ((Post) baseQuickAdapter.getData().get(i)).getPostImg());
                                startActivity(intent);

                                break;
                            case R.id.menu_post_owner_delete:
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainTimelineFragment.this.getContext());
                                builder.setMessage("삭제하시겠습니까?")
                                        .setPositiveButton("확인", (dialog, which) -> {
                                            baseQuickAdapter.getData().remove(i);
                                            baseQuickAdapter.notifyItemRemoved(i);
                                            dialog.dismiss();
                                        })
                                        .setNegativeButton("취소", (dialog, which) -> dialog.dismiss())
                                        .show();
                                break;
                        }
                        return false;
                    });
                    popup.show();
                    break;

                case R.id.timeline_user_group:
                    SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                    try {
                        String item_userid = ((Post) baseQuickAdapter.getData().get(i)).getUserID();
                        String active_userid = new JSONObject(mSharedPreferences.getString(getString(R.string.SP_USERINFO), null)).getString("user_id");

                        Intent intent = new Intent(getContext(), HomeActivity.class);
                        if (item_userid.equals(active_userid))
                            intent.putExtra("MODE",HomeActivity.MODE.MY);
                        else {
                            intent.putExtra("MODE",HomeActivity.MODE.USER);
                            intent.putExtra("UserID", ((Post) baseQuickAdapter.getData().get(i)).getUserID());
                        }
                        startActivity(intent);
                    } catch (JSONException e) {
                        Utils.log(e.toString());
                    }
                    break;
            }
        });

        timeLineService = new Retrofit.Builder()
                .baseUrl(getString(R.string.server_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(RetrofitServices.TimeLineService.class);
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
        new Handler().postDelayed(() -> {
            postListAdapter.setNewData(postList);
            swipeRefreshLayout.setRefreshing(false);
            postListAdapter.setEnableLoadMore(true);
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
                        });
            }
        }
    }

    class PostListAdapter extends BaseQuickAdapter<Post, BaseViewHolder> {
        private final RequestManager requestManager;

        PostListAdapter(RequestManager requestManager) {
            super(R.layout.li_timeline);
            this.requestManager = requestManager;
        }

        @Override
        protected void convert(BaseViewHolder helper, Post item) {
            requestManager
                    .load(getString(R.string.storage_image_url_profile) + item.getUserImg())
//                    .apply(RequestOptions.placeholderOf(getContext().getDrawable(R.raw.dummy_profile_0)).centerCrop())
                    .into((AppCompatImageView) helper.getView(R.id.timeline_userimg));

            requestManager
                    .load(getString(R.string.storage_image_url_post) + item.getPostImg())
                    .apply(RequestOptions.centerCropTransform())
                    .into((AppCompatImageView) helper.itemView.findViewById(R.id.timeline_postimg));

            helper.setText(R.id.timeline_nickname, item.getNickName());
            helper.setText(R.id.timeline_likecount, "0");
            helper.setText(R.id.timeline_contents, " " + item.getContents());
            helper.setText(R.id.timeline_time, "1일전");
            helper.addOnClickListener(R.id.timeline_like);
            helper.addOnClickListener(R.id.timeline_comment);
            helper.addOnClickListener(R.id.timeline_other);
            helper.addOnClickListener(R.id.timeline_user_group);

            ((FlexboxLayout) helper.getView(R.id.timeline_tags)).removeAllViews();
            String[] tags = {"tag1", "tag2", "tag3", "tag4"};

            for (String tag : tags) {
                AppCompatTextView textView = new AppCompatTextView(getContext());
                textView.setText("#" + tag + " ");
                textView.setTextColor(getResources().getColor(R.color.colorPrimary,null));

                ((FlexboxLayout) helper.getView(R.id.timeline_tags)).addView(textView);
            }

            ((FlexboxLayout) helper.getView(R.id.timeline_commentdetail)).removeAllViews();
            List<PostComment> aa = new ArrayList<>();
            PostComment bb = new PostComment();
            bb.setNickName("유저1");
            bb.setContents("댓글1\n댓글1\n댓글1\n댓글1");
            bb.setDateTime(0);

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