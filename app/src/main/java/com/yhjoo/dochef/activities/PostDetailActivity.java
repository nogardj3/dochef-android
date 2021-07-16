package com.yhjoo.dochef.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.JsonObject;
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.adapter.CommentListAdapter;
import com.yhjoo.dochef.databinding.APostdetailBinding;
import com.yhjoo.dochef.interfaces.RetrofitServices;
import com.yhjoo.dochef.model.Comment;
import com.yhjoo.dochef.model.Post;
import com.yhjoo.dochef.utils.BasicCallback;
import com.yhjoo.dochef.utils.DataGenerator;
import com.yhjoo.dochef.utils.GlideApp;
import com.yhjoo.dochef.utils.RetrofitBuilder;
import com.yhjoo.dochef.utils.Utils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

public class PostDetailActivity extends BaseActivity {
    APostdetailBinding binding;
    RetrofitServices.PostService postService;
    RetrofitServices.CommentService commentService;
    CommentListAdapter commentListAdapter;

    ArrayList<Comment> commentList;
    Post postInfo;

    String userID;
    int postID;

    /*
        TODO
        group은 onclick 안됨 수정
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = APostdetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.postToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        postService = RetrofitBuilder.create(this, RetrofitServices.PostService.class);
        commentService = RetrofitBuilder.create(this, RetrofitServices.CommentService.class);

        userID = Utils.getUserBrief(this).getUserID();
        postID = getIntent().getIntExtra("postID", -1);

        commentListAdapter = new CommentListAdapter(userID);
        commentListAdapter.setOnItemChildClickListener((baseQuickAdapter, view, position) -> {
            PopupMenu popup = new PopupMenu(PostDetailActivity.this, view);
            getMenuInflater().inflate(R.menu.menu_comment_owner, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(PostDetailActivity.this);
                builder.setMessage("삭제 하시겠습니까?")
                        .setPositiveButton("확인", (dialog, which) -> {
                            dialog.dismiss();
                            removeComment(((Comment) baseQuickAdapter.getItem(position)).getCommentID());
                        })
                        .setNegativeButton("취소", (dialog, which) -> dialog.dismiss())
                        .show();
                return false;
            });
            popup.show();
        });
        binding.postCommentRecycler.setLayoutManager(new LinearLayoutManager(this));
        binding.postCommentRecycler.setAdapter(commentListAdapter);

        if (App.isServerAlive()) {
            getPostInfo(postID);
            getCommentList(postID);
        } else {
            postInfo = ((ArrayList<Post>) DataGenerator.make(getResources(), getResources().getInteger(R.integer.DATA_TYPE_POST))).get(0);
            commentList = DataGenerator.make(getResources(), getResources().getInteger(R.integer.DATA_TYPE_COMMENTS));

            setTopView();
            commentListAdapter.setNewData(commentList);
        }
    }

    void getPostInfo(int postID) {
        postService.getPost(postID)
                .enqueue(new BasicCallback<Post>(this) {
                    @Override
                    public void onResponse(Call<Post> call, Response<Post> response) {
                        super.onResponse(call, response);

                        if (response.code() == 500) {
                            App.getAppInstance().showToast("post detail 가져오기 실패");
                        } else {
                            postInfo = response.body();
                            setTopView();
                        }
                    }
                });
    }

    void setTopView() {
        if (App.isServerAlive()) {
            if (!postInfo.getPostImg().equals("")) {
                binding.postPostimg.setVisibility(View.VISIBLE);

                StorageReference sr = FirebaseStorage
                        .getInstance().getReference().child("post/" + postInfo.getPostImg());
                GlideApp.with(this)
                        .load(sr)
                        .into(binding.postPostimg);
            }
            if (!postInfo.getUserImg().equals("default")) {
                StorageReference sr = FirebaseStorage
                        .getInstance().getReference().child("profile/" + postInfo.getUserImg());
                GlideApp.with(this)
                        .load(sr)
                        .circleCrop()
                        .into(binding.postUserimg);
            }
        } else {
            binding.postPostimg.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(Integer.parseInt(postInfo.getPostImg()))
                    .into(binding.postPostimg);

            Glide.with(this)
                    .load(Integer.parseInt(postInfo.getUserImg()))
                    .circleCrop()
                    .into(binding.postUserimg);
        }

        binding.postNickname.setText(postInfo.getNickname());
        binding.postContents.setText(postInfo.getContents());
        binding.postTime.setText(Utils.convertMillisToText(postInfo.getDateTime()));
        binding.postLikecount.setText(Integer.toString(postInfo.getLikes().size()));
        binding.postCommentcount.setText(Integer.toString(postInfo.getComments().size()));
        binding.postUserGroup.setOnClickListener(v -> {
            Intent intent = new Intent(PostDetailActivity.this, HomeActivity.class)
                    .putExtra("userID", postInfo.getUserID());
            startActivity(intent);
        });

        if (postInfo.getLikes().contains(userID))
            binding.postLike.setImageResource(R.drawable.ic_favorite_red);
        else
            binding.postLike.setImageResource(R.drawable.ic_favorite_black);

        binding.postLike.setOnClickListener(v -> toggleLikePost(userID, postID));
        binding.postOther.setVisibility(postInfo.getUserID().equals(userID) ? View.VISIBLE : View.GONE);
        binding.postOther.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(PostDetailActivity.this, v);
            PostDetailActivity.this.getMenuInflater().inflate(R.menu.menu_post_owner, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.menu_post_owner_revise:
                        Intent intent = new Intent(PostDetailActivity.this, PostWriteActivity.class)
                                .putExtra("MODE", PostWriteActivity.MODE.REVISE)
                                .putExtra("postID", postInfo.getPostID())
                                .putExtra("postimg", postInfo.getPostImg())
                                .putExtra("contents", postInfo.getContents())
                                .putExtra("tags", postInfo.getTags());
                        startActivity(intent);
                        break;
                    case R.id.menu_post_owner_delete:
                        App.getAppInstance().showToast("삭제");
                        AlertDialog.Builder builder = new AlertDialog.Builder(PostDetailActivity.this);
                        builder.setMessage("삭제하시겠습니까?")
                                .setPositiveButton("확인", (dialog, which) -> {
                                    deletePost(postID);
                                    dialog.dismiss();
                                    finish();
                                })
                                .setNegativeButton("취소", (dialog, which) -> dialog.dismiss())
                                .show();
                        break;
                }
                return false;
            });
            popup.show();
        });

        binding.postTags.removeAllViews();
        for (String tag : postInfo.getTags()) {
            LinearLayout tagcontainer = (LinearLayout) getLayoutInflater().inflate(R.layout.v_tag_post, null);
            AppCompatTextView tagview = tagcontainer.findViewById(R.id.vtag_post_text);
            tagview.setText("#" + tag);
            binding.postTags.addView(tagcontainer);
        }

        binding.postCommentOk.setOnClickListener(this::writeComment);
    }

    void getCommentList(int postID) {
        commentService.getComment(postID)
                .enqueue(new BasicCallback<ArrayList<Comment>>(this) {
                    @Override
                    public void onResponse(Call<ArrayList<Comment>> call, Response<ArrayList<Comment>> response) {
                        super.onResponse(call, response);

                        if (response.code() == 500) {
                            App.getAppInstance().showToast("comment 가져오기 실패");
                        } else {
                            commentList = response.body();
                            commentListAdapter.setNewData(commentList);
                            commentListAdapter.notifyDataSetChanged();
                            commentListAdapter.setEmptyView(R.layout.rv_empty_comment,
                                    (ViewGroup) binding.postCommentRecycler.getParent());
                        }
                    }
                });
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

        postService.setLikePost(userID, postID, new_like)
                .enqueue(new BasicCallback<JsonObject>(this) {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        super.onResponse(call, response);
                        progressOFF();

                        if (response.code() == 500) {
                            App.getAppInstance().showToast("like toggle 실패");
                        } else
                            App.getAppInstance().showToast("like toggle 성공");

                        getPostInfo(postID);
                    }
                });
    }

    void deletePost(int postID) {
        postService.deletePost(postID)
                .enqueue(new BasicCallback<JsonObject>(this) {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        super.onResponse(call, response);

                        if (response.code() == 500) {
                            App.getAppInstance().showToast("delete post 실패");
                        } else
                            App.getAppInstance().showToast("delete post 성공");
                    }
                });
    }

    void writeComment(View v) {
        if (!binding.postCommentEdittext.getText().toString().equals("")) {
            commentService.createComment(postID, userID,
                    binding.postCommentEdittext.getText().toString(), System.currentTimeMillis())
                    .enqueue(new BasicCallback<JsonObject>(this) {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            super.onResponse(call, response);

                            if (response.code() == 500) {
                                App.getAppInstance().showToast("comment 생성 실패");
                            } else {
                                App.getAppInstance().showToast("comment 생성 성공");
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(binding.postCommentEdittext.getWindowToken(), 0);
                                binding.postCommentEdittext.setText("");

                                getCommentList(postID);
                            }
                        }
                    });

        } else
            App.getAppInstance().showToast("댓글을 입력 해 주세요");
    }

    void removeComment(int commentID) {
        commentService.deleteComment(commentID)
                .enqueue(new BasicCallback<JsonObject>(this) {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        super.onResponse(call, response);

                        if (response.code() == 500) {
                            App.getAppInstance().showToast("comment 삭제 실패");
                        } else {
                            App.getAppInstance().showToast("comment 삭제 성공");
                            getCommentList(postID);
                        }
                    }
                });

    }
}