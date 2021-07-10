package com.yhjoo.dochef.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.JsonObject;
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.adapter.CommentListAdapter;
import com.yhjoo.dochef.databinding.ACommentBinding;
import com.yhjoo.dochef.interfaces.RetrofitServices;
import com.yhjoo.dochef.model.Comment;
import com.yhjoo.dochef.utils.BasicCallback;
import com.yhjoo.dochef.utils.DummyMaker;
import com.yhjoo.dochef.utils.RetrofitBuilder;
import com.yhjoo.dochef.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

public class CommentActivity extends BaseActivity {
    ACommentBinding binding;
    RetrofitServices.CommentService commentService;
    CommentListAdapter commentListAdapter;

    String userID = "";
    int postID = -1;

    /*
        TODO
        이거 필요 없는듯 -> postdetail로 합치기

        1. 대댓글은 없음 -> 시간없음
        2. retrofit 정리
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ACommentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.commentToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        try {
            JSONObject userInfo = new JSONObject(mSharedPreferences.getString(getString(R.string.SP_USERINFO), null));
            userID = userInfo.getString("user_id");
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        postID = getIntent().getIntExtra("postID", 0);

        commentService = RetrofitBuilder.create(this, RetrofitServices.CommentService.class);

        binding.footerCommentEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.footerCommentClear.setVisibility(count == 0 ? View.GONE : View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        binding.footerCommentOk.setOnClickListener(this::writeComment);
        binding.footerCommentClear.setOnClickListener(v -> binding.footerCommentEdittext.setText(""));

        commentListAdapter = new CommentListAdapter(userID);
        commentListAdapter.setEmptyView(R.layout.rv_loading, (ViewGroup) binding.commentRecycler.getParent());
        commentListAdapter.setOnItemChildClickListener((baseQuickAdapter, view, position) -> {
            PopupMenu popup = new PopupMenu(CommentActivity.this, view);
            getMenuInflater().inflate(R.menu.menu_comment_owner, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.menu_comment_owner_delete:
                        AlertDialog.Builder builder = new AlertDialog.Builder(CommentActivity.this);
                        builder.setMessage("삭제 하시겠습니까?")
                                .setPositiveButton("확인", (dialog, which) -> {
                                    dialog.dismiss();
                                    removeComment(((Comment) baseQuickAdapter.getItem(position)).getCommentID());
                                })
                                .setNegativeButton("취소", (dialog, which) -> dialog.dismiss())
                                .show();
                        break;
                }
                return false;
            });
            popup.show();
        });

        // SERVER DATA
        if (App.isServerAlive())
            getCommentList(postID);
        // DUMMY DATA
        else
            commentListAdapter.setNewData(DummyMaker.make(getResources(), getResources().getInteger(R.integer.DUMMY_TYPE_COMMENTS)));

        binding.commentRecycler.setLayoutManager(new LinearLayoutManager(this));
        binding.commentRecycler.setAdapter(commentListAdapter);
    }

    void getCommentList(int postID) {
        commentService.getComment(postID).enqueue(new BasicCallback<ArrayList<Comment>>(this) {
            @Override
            public void onResponse(Call<ArrayList<Comment>> call, Response<ArrayList<Comment>> response) {
                super.onResponse(call, response);

                if (response.code() == 500) {
                    App.getAppInstance().showToast("comment 가져오기 실패");
                } else{
                    App.getAppInstance().showToast("comment 가져오기 성공");
                    commentListAdapter.setNewData(response.body());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Comment>> call, Throwable t) {
                super.onFailure(call, t);
            }
        });
    }

    void writeComment(View v) {
        progressON(this);

        if (!binding.footerCommentEdittext.getText().toString().equals("")) {
            commentService.createComment(postID, userID,
                    binding.footerCommentEdittext.getText().toString(), System.currentTimeMillis())
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

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            super.onFailure(call, t);
                        }
                    });

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(binding.footerCommentEdittext.getWindowToken(), 0);
            binding.footerCommentEdittext.setText("");

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

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        super.onFailure(call, t);
                    }
                });

        getCommentList(postID);
    }
}
