package com.yhjoo.dochef.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.flexbox.FlexboxLayout;
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.base.BaseActivity;
import com.yhjoo.dochef.classes.Post;
import com.yhjoo.dochef.classes.PostComment;
import com.yhjoo.dochef.interfaces.RetrofitServices;
import com.yhjoo.dochef.utils.BasicCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PostDetailActivity extends BaseActivity {
    private Post post;

    enum OPERATION {VIEW, REVISE}

    /*
        TODO
        1. OPERATION 분기
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_post);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.post_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        post = (Post) getIntent().getSerializableExtra("post");

        //temp
        if (post == null) {
            RetrofitServices.TimeLineService timeLineService = new Retrofit.Builder()
                    .baseUrl(getString(R.string.server_url))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(RetrofitServices.TimeLineService.class);

            timeLineService.GetPostCall(0)
                    .enqueue(new BasicCallback<ArrayList<Post>>(PostDetailActivity.this) {
                        @Override
                        public void onResponse(Response<ArrayList<Post>> response) {
                            post = response.body().get(0);

                            RetrofitServices.PostActivityService postActivityService = new Retrofit.Builder()
                                    .baseUrl(getString(R.string.server_url))
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .build().create(RetrofitServices.PostActivityService.class);

                            postActivityService.GetCommentCall(post.getPostID())
                                    .enqueue(new BasicCallback<ArrayList<PostComment>>(PostDetailActivity.this) {
                                        @Override
                                        public void onResponse(Response<ArrayList<PostComment>> response) {
                                            setheaderfooter();
                                        }
                                    });
                        }
                    });
        } else {
            RetrofitServices.PostActivityService postActivityService = new Retrofit.Builder()
                    .baseUrl(getString(R.string.server_url))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(RetrofitServices.PostActivityService.class);

            postActivityService.GetCommentCall(post.getPostID())
                    .enqueue(new BasicCallback<ArrayList<PostComment>>(PostDetailActivity.this) {
                        @Override
                        public void onResponse(Response<ArrayList<PostComment>> response) {
                            setheaderfooter();
                        }
                    });
        }
    }

    /*
        TODO
        1. 더미 만들기
        2. retrofit 구현
    */

    private void setheaderfooter() {
        Glide.with(this)
                .load("https://s3.ap-northeast-2.amazonaws.com/quvechefbucket/postImage/" + post.getPostImg())
                .apply(RequestOptions.centerCropTransform())
                .into((AppCompatImageView) findViewById(R.id.post_postimg));

        Glide.with(this)
                .load("https://s3.ap-northeast-2.amazonaws.com/quvechefbucket/profile/" + post.getUserImg())
                .apply(RequestOptions.placeholderOf(R.drawable.ic_person_black_24dp).circleCrop())
                .into((AppCompatImageView) findViewById(R.id.post_userimg));

        ((AppCompatTextView) findViewById(R.id.post_nickname)).setText(post.getNickName());
        ((AppCompatTextView) findViewById(R.id.post_likecount)).setText("1623");
        ((AppCompatTextView) findViewById(R.id.post_contents)).setText(post.getContents());
        ((AppCompatTextView) findViewById(R.id.post_time)).setText("1일전");

        findViewById(R.id.post_like).setOnClickListener(v -> App.getAppInstance().showToast("좋아요"));
        findViewById(R.id.post_comment).setOnClickListener(v -> startActivity(new Intent(PostDetailActivity.this, CommentActivity.class)));
        findViewById(R.id.post_other).setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(PostDetailActivity.this, v);
            // if(ismaster)
            PostDetailActivity.this.getMenuInflater().inflate(R.menu.menu_post_owner, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.menu_post_owner_revise:
                        Intent intent = new Intent(PostDetailActivity.this, PostReviseActivity.class);
                        intent.putExtra("postid", post.getPostID())
                                .putExtra("contents", post.getContents())
                                .putExtra("postimg", "https://s3.ap-northeast-2.amazonaws.com/quvechefbucket/postImage/" + post.getPostImg());
                        startActivity(intent);
                        break;
                    case R.id.menu_post_owner_delete:
                        App.getAppInstance().showToast("삭제");
                        AlertDialog.Builder builder = new AlertDialog.Builder(PostDetailActivity.this);
                        builder.setMessage("삭제하시겠습니까?")
                                .setPositiveButton("확인", (dialog, which) -> {
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

        findViewById(R.id.post_user_layout).setOnClickListener(v -> {
            SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            try {
                if (mSharedPreferences.getBoolean(getString(R.string.SHAREDPREFERENCE_AUTOLOGIN), false) &&
                        post.getUserID().equals(new JSONObject(mSharedPreferences.getString(getString(R.string.SHAREDPREFERENCE_USERINFO), null)).getString("USER_ID"))) {

                    Intent intent = new Intent(PostDetailActivity.this, HomeActivity.class);
                    startActivity(intent);

                } else {
                    Intent intent = new Intent(PostDetailActivity.this, HomeUserActivity.class);
                    intent.putExtra("UserID", post.getUserID());
                    startActivity(intent);
                }
            } catch (JSONException e) {
                Intent intent = new Intent(PostDetailActivity.this, HomeUserActivity.class);
                intent.putExtra("UserID", post.getUserID());
                startActivity(intent);
            }
        });

        ((FlexboxLayout) findViewById(R.id.post_tags)).removeAllViews();
        String[] tags = {"tag1", "tag2", "tag3", "tag4"};

        for (String tag : tags) {
            AppCompatTextView textView = new AppCompatTextView(PostDetailActivity.this);
            textView.setText("#" + tag + " ");
            textView.setTextColor(getResources().getColor(R.color.colorPrimary,null));

            ((FlexboxLayout) findViewById(R.id.post_tags)).addView(textView);
        }


        ((FlexboxLayout) findViewById(R.id.post_commentdetail)).removeAllViews();
        List<PostComment> aa = new ArrayList<>();
        PostComment bb = new PostComment();
        bb.setNickName("유저1");
        bb.setContents("댓글1\n댓글1\n댓글1\n댓글1");
        bb.setDateTime(0);

        aa.add(bb);
        aa.add(bb);

        if (aa.size() != 0) {
            findViewById(R.id.post_comment_layout).setVisibility(View.VISIBLE);
            for (int i = 0; i < aa.size(); i++) {
                @SuppressLint("InflateParams") LinearLayout motherview = (LinearLayout) getLayoutInflater().inflate(R.layout.li_comment_brief, null);
                AppCompatTextView view1 = ((AppCompatTextView) motherview.findViewById(R.id.li_comment_brief_nickname));
                view1.setText(aa.get(i).getNickName());
                AppCompatTextView view2 = ((AppCompatTextView) motherview.findViewById(R.id.li_comment_brief_contents));
                view2.setText(aa.get(i).getContents());
                AppCompatTextView view3 = ((AppCompatTextView) motherview.findViewById(R.id.li_comment_brief_date));
                view3.setText("1일전");
                motherview.setOnClickListener(v -> startActivity(new Intent(PostDetailActivity.this, CommentActivity.class)));

                ((FlexboxLayout) findViewById(R.id.post_commentdetail)).addView(motherview);
            }
        } else {
            findViewById(R.id.post_comment_layout).setVisibility(View.GONE);
        }
    }

}