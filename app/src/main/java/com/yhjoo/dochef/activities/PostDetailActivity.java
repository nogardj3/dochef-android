package com.yhjoo.dochef.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.JsonObject;
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.adapter.CommentListAdapter;
import com.yhjoo.dochef.databinding.APostdetailBinding;
import com.yhjoo.dochef.interfaces.RetrofitServices;
import com.yhjoo.dochef.model.Comment;
import com.yhjoo.dochef.model.Post;
import com.yhjoo.dochef.utils.BasicCallback;
import com.yhjoo.dochef.utils.RetrofitBuilder;
import com.yhjoo.dochef.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

public class PostDetailActivity extends BaseActivity {
    APostdetailBinding binding;
    RetrofitServices.PostService postService;
    RetrofitServices.CommentService commentService;
    CommentListAdapter commentListAdapter;

    Post postInfo;
    String userID;
    int postID;

    /*
        TODO
        postDetail = 수정 O, 댓글 많이, 댓글 작성 가능
        timeline   = 수정 X, 댓글 하나, 댓글 작성 불가

        1. tags 확인
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

        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        try {
            JSONObject aa = new JSONObject(mSharedPreferences.getString(getString(R.string.SP_USERINFO), null));
            userID = aa.getString("user_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        postID = getIntent().getIntExtra("postID", -1);

        getPostInfo(postID);
        getCommentList(postID);
    }

    void getPostInfo(int postID) {
        progressON(this);
        postService.getPost(postID)
                .enqueue(new BasicCallback<Post>(this) {
                    @Override
                    public void onResponse(Call<Post> call, Response<Post> response) {
                        super.onResponse(call, response);
                        progressOFF();

                        if (response.code() == 500) {
                            App.getAppInstance().showToast("post detail 가져오기 실패");
                        } else {
                            postInfo = response.body();

                            setTopView(postInfo);
                        }
                    }
                });
    }

    void setTopView(Post postInfo) {
        if (!postInfo.getPostImg().equals("default")) {
            binding.postPostimg.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(getString(R.string.storage_image_url_post) + postInfo.getPostImg())
                    .apply(RequestOptions.centerCropTransform())
                    .into(binding.postPostimg);
        }

        if (!postInfo.getUserImg().equals("default"))
            Glide.with(this)
                    .load(getString(R.string.storage_image_url_profile) + postInfo.getUserImg())
                    .into(binding.postUserimg);

        binding.postNickname.setText(postInfo.getNickname());
        binding.postUserGroup.setOnClickListener(v -> {
            Intent intent = new Intent(PostDetailActivity.this, HomeActivity.class);
            if (postInfo.getUserID().equals(userID))
                intent.putExtra("MODE", HomeActivity.MODE.MY);
            else {
                intent.putExtra("MODE", HomeActivity.MODE.USER);
                intent.putExtra("userID", postInfo.getUserID());
            }
            startActivity(intent);
        });

        binding.postLikecount.setText(Integer.toString(postInfo.getLikes().size()));
        binding.postContents.setText(postInfo.getContents());
        binding.postTime.setText(Utils.convertMillisToText(postInfo.getDateTime()));

        if (postInfo.getLikes().contains(userID))
            binding.postLike.setImageResource(R.drawable.ic_favorite_24dp);
        else
            binding.postLike.setImageResource(R.drawable.ic_favorite_border_black_24dp);

        binding.postLike.setOnClickListener(v -> toggleLikePost(userID, postID));
        binding.postOther.setVisibility(postInfo.getUserID().equals(userID) ? View.VISIBLE : View.GONE);
        binding.postOther.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(PostDetailActivity.this, v);
            PostDetailActivity.this.getMenuInflater().inflate(R.menu.menu_post_owner, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.menu_post_owner_revise:
                        Intent intent = new Intent(PostDetailActivity.this, PostWriteActivity.class);
                        intent.putExtra("MODE", PostWriteActivity.MODE.REVISE)
                                .putExtra("postID", postInfo.getPostID())
                                .putExtra("postimg", getString(R.string.storage_image_url_post) + postInfo.getPostImg())
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
        binding.postTags.setTagList(postInfo.getTags());

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
                            setCommentView(response.body());
                        }
                        commentListAdapter.setEmptyView(R.layout.rv_comment_empty, (ViewGroup) binding.postCommentRecycler.getParent());
                    }
                });
    }

    void setCommentView(ArrayList<Comment> commentList) {
        commentListAdapter = new CommentListAdapter(userID);
        commentListAdapter.setEmptyView(R.layout.rv_loading, (ViewGroup) binding.postCommentRecycler.getParent());
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
        commentListAdapter.setNewData(commentList);

        binding.postCommentRecycler.setLayoutManager(new LinearLayoutManager(this));
        binding.postCommentRecycler.setAdapter(commentListAdapter);
    }

    void toggleLikePost(String userID, int postID) {
        int new_like;
        Utils.log(postInfo.toString());
        if (!postInfo.getLikes().contains(userID)) {
            new_like = 1;
            binding.postLike.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        } else {
            new_like = 0;
            binding.postLike.setImageResource(R.drawable.ic_favorite_24dp);
        }

        postService.likePost(userID, postID, new_like)
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
                        progressOFF();

                        if (response.code() == 500) {
                            App.getAppInstance().showToast("delete post 실패");
                        } else
                            App.getAppInstance().showToast("delete post 성공");
                    }
                });
    }

    void writeComment(View v) {
        if (!binding.postCommentEdittext.getText().toString().equals("")) {
            progressON(this);
            commentService.createComment(postID, userID,
                    binding.postCommentEdittext.getText().toString(), System.currentTimeMillis())
                    .enqueue(new BasicCallback<JsonObject>(this) {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            super.onResponse(call, response);
                            progressOFF();

                            if (response.code() == 500) {
                                App.getAppInstance().showToast("comment 생성 실패");
                            } else
                                App.getAppInstance().showToast("comment 생성 성공");
                        }
                    });

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(binding.postCommentEdittext.getWindowToken(), 0);
            binding.postCommentEdittext.setText("");

            getCommentList(postID);
        } else
            App.getAppInstance().showToast("댓글을 입력 해 주세요");
    }

    void removeComment(int commentID) {
        progressON(this);

        commentService.deleteComment(commentID)
                .enqueue(new BasicCallback<JsonObject>(this) {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        super.onResponse(call, response);
                        progressOFF();

                        if (response.code() == 500) {
                            App.getAppInstance().showToast("comment 삭제 실패");
                        } else
                            App.getAppInstance().showToast("comment 삭제 성공");
                    }
                });

        getCommentList(postID);
    }
}