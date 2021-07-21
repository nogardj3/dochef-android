package com.yhjoo.dochef.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.adapter.CommentListAdapter;
import com.yhjoo.dochef.databinding.APostdetailBinding;
import com.yhjoo.dochef.interfaces.RxRetrofitServices;
import com.yhjoo.dochef.model.Comment;
import com.yhjoo.dochef.model.Post;
import com.yhjoo.dochef.utils.DataGenerator;
import com.yhjoo.dochef.utils.ImageLoadUtil;
import com.yhjoo.dochef.utils.RxRetrofitBuilder;
import com.yhjoo.dochef.utils.Utils;

import java.util.ArrayList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.functions.Function;
import retrofit2.Response;

public class PostDetailActivity extends BaseActivity {
    APostdetailBinding binding;
    RxRetrofitServices.PostService postService;
    RxRetrofitServices.CommentService commentService;
    CommentListAdapter commentListAdapter;

    ArrayList<Comment> commentList;
    Post postInfo;

    String userID;
    int postID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = APostdetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.postToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        postService = RxRetrofitBuilder.create(this, RxRetrofitServices.PostService.class);
        commentService = RxRetrofitBuilder.create(this, RxRetrofitServices.CommentService.class);

        userID = Utils.getUserBrief(this).getUserID();
        postID = getIntent().getIntExtra("postID", -1);

        commentListAdapter = new CommentListAdapter(userID);
        commentListAdapter.setOnItemChildClickListener((baseQuickAdapter, view, position) -> {
            PowerMenu powerMenu = new PowerMenu.Builder(this)
                    .addItem(new PowerMenuItem("삭제", false))
                    .setAnimation(MenuAnimation.SHOW_UP_CENTER)
                    .setMenuRadius(10f)
                    .setMenuShadow(5.0f)
                    .setWidth(200)
                    .setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                    .setTextGravity(Gravity.CENTER)
                    .setMenuColor(Color.WHITE)
                    .setBackgroundAlpha(0f)
                    .build();

            powerMenu.setOnMenuItemClickListener((pos, item) -> {
                if (pos == 0) {
                    createConfirmDialog(this,
                            null, "삭제 하시겠습니까?", (dialog1, which) ->
                                    removeComment(((Comment) baseQuickAdapter.getItem(position)).getCommentID()))
                            .show();

                    powerMenu.dismiss();
                }
            });

            powerMenu.showAsAnchorCenter(view);
        });
        binding.postCommentRecycler.setLayoutManager(new LinearLayoutManager(this));
        binding.postCommentRecycler.setAdapter(commentListAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (App.isServerAlive())
            loadData();
        else {
            postInfo = ((ArrayList<Post>) DataGenerator.make(getResources(), getResources().getInteger(R.integer.DATA_TYPE_POST))).get(0);
            commentList = DataGenerator.make(getResources(), getResources().getInteger(R.integer.DATA_TYPE_COMMENTS));

            setTopView();
            commentListAdapter.setNewData(commentList);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (postInfo.getUserID().equals(userID))
            getMenuInflater().inflate(R.menu.menu_post_owner, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_post_owner_revise) {
            Intent intent = new Intent(PostDetailActivity.this, PostWriteActivity.class)
                    .putExtra("MODE", PostWriteActivity.MODE.REVISE)
                    .putExtra("postID", postInfo.getPostID())
                    .putExtra("postImg", postInfo.getPostImg())
                    .putExtra("contents", postInfo.getContents())
                    .putExtra("tags", postInfo.getTags().toArray(new String[0]));
            startActivity(intent);
        } else if (item.getItemId() == R.id.menu_post_owner_delete) {
            App.getAppInstance().showToast("삭제");
            createConfirmDialog(this,
                    null, "삭제하시겠습니까?",
                    (dialog1, which) -> compositeDisposable.add(
                            postService.deletePost(postID)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(response -> finish()
                                            , RxRetrofitBuilder.defaultConsumer())
                    )).show();
        }

        return super.onOptionsItemSelected(item);
    }

    void loadData() {
        compositeDisposable.add(
                postService.getPost(postID)
                        .flatMap((Function<Response<Post>, Single<Response<ArrayList<Comment>>>>)
                                response -> {
                                    postInfo = response.body();
                                    return commentService.getComment(postID)
                                            .observeOn(AndroidSchedulers.mainThread());
                                }
                        )
                        .subscribe(response -> {
                            commentList = response.body();

                            setTopView();
                            commentListAdapter.setNewData(commentList);
                            commentListAdapter.setEmptyView(R.layout.rv_empty_comment,
                                    (ViewGroup) binding.postCommentRecycler.getParent());

                        }, RxRetrofitBuilder.defaultConsumer())

        );
    }

    void setTopView() {
        ImageLoadUtil.loadPostImage(this, postInfo.getPostImg(), binding.postPostimg);
        ImageLoadUtil.loadUserImage(this, postInfo.getUserImg(), binding.postUserimg);

        binding.postNickname.setText(postInfo.getNickname());
        binding.postContents.setText(postInfo.getContents());
        binding.postTime.setText(Utils.convertMillisToText(postInfo.getDateTime()));
        binding.postLikecount.setText(Integer.toString(postInfo.getLikes().size()));
        binding.postCommentcount.setText(Integer.toString(postInfo.getComments().size()));
        binding.postUserWrapper.setOnClickListener(v -> {
            Intent intent = new Intent(PostDetailActivity.this, HomeActivity.class)
                    .putExtra("userID", postInfo.getUserID());
            startActivity(intent);
        });

        if (postInfo.getLikes().contains(userID))
            binding.postLike.setImageResource(R.drawable.ic_favorite_red);
        else
            binding.postLike.setImageResource(R.drawable.ic_favorite_black);

        binding.postLike.setOnClickListener(v -> toggleLikePost(userID, postID));

        binding.postTags.removeAllViews();
        for (String tag : postInfo.getTags()) {
            LinearLayout tagcontainer = (LinearLayout) getLayoutInflater().inflate(R.layout.v_tag_post, null);
            AppCompatTextView tagview = tagcontainer.findViewById(R.id.vtag_post_text);
            tagview.setText("#" + tag);
            binding.postTags.addView(tagcontainer);
        }

        binding.postCommentOk.setOnClickListener(this::writeComment);
    }

    void toggleLikePost(String userID, int postID) {
        int new_like;
        if (!postInfo.getLikes().contains(userID)) {
            new_like = 1;
            binding.postLike.setImageResource(R.drawable.ic_favorite_black);
        } else {
            new_like = -1;
            binding.postLike.setImageResource(R.drawable.ic_favorite_red);
        }

        compositeDisposable.add(
                postService.setLikePost(userID, postID, new_like)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> loadData(), RxRetrofitBuilder.defaultConsumer())
        );
    }

    void writeComment(View v) {
        if (!binding.postCommentEdittext.getText().toString().equals("")) {
            compositeDisposable.add(
                    commentService.createComment(postID, userID,
                            binding.postCommentEdittext.getText().toString(), System.currentTimeMillis())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(response -> {
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(binding.postCommentEdittext.getWindowToken(), 0);
                                binding.postCommentEdittext.setText("");
                                loadData();
                            }, RxRetrofitBuilder.defaultConsumer())
            );
        } else
            App.getAppInstance().showToast("댓글을 입력 해 주세요");
    }

    void removeComment(int commentID) {
        compositeDisposable.add(
                commentService.deleteComment(commentID)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> loadData(), RxRetrofitBuilder.defaultConsumer())
        );
    }
}