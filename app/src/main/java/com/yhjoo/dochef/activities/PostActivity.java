package com.yhjoo.dochef.activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import com.yhjoo.dochef.Preferences;
import com.yhjoo.dochef.DoChef;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.base.BaseActivity;
import com.yhjoo.dochef.classes.Post;
import com.yhjoo.dochef.classes.PostComment;
import com.yhjoo.dochef.utils.BasicCallback;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class PostActivity extends BaseActivity {
    private Post post;

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
            TimeLineService timeLineService = new Retrofit.Builder()
                    .baseUrl(getString(R.string.server_url))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(TimeLineService.class);

            timeLineService.GetPostCall(0)
                    .enqueue(new BasicCallback<ArrayList<Post>>(PostActivity.this) {
                        @Override
                        public void onResponse(Response<ArrayList<Post>> response) {
                            post = response.body().get(0);

                            PostActivityService postActivityService = new Retrofit.Builder()
                                    .baseUrl(getString(R.string.server_url))
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .build().create(PostActivityService.class);

                            postActivityService.GetCommentCall(post.getPostID())
                                    .enqueue(new BasicCallback<ArrayList<PostComment>>(PostActivity.this) {
                                        @Override
                                        public void onResponse(Response<ArrayList<PostComment>> response) {
                                            setheaderfooter();
                                        }
                                    });
                        }

                        @Override

                        public void onFailure() {
                        }
                    });
        } else {
            PostActivityService postActivityService = new Retrofit.Builder()
                    .baseUrl(getString(R.string.server_url))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(PostActivityService.class);

            postActivityService.GetCommentCall(post.getPostID())
                    .enqueue(new BasicCallback<ArrayList<PostComment>>(PostActivity.this) {
                        @Override
                        public void onResponse(Response<ArrayList<PostComment>> response) {
                            setheaderfooter();
                        }
                    });
        }
    }

    private void setheaderfooter() {
        Glide.with(this)
                .load("https://s3.ap-northeast-2.amazonaws.com/quvechefbucket/postImage/" + post.getPostImg())
                .apply(RequestOptions.centerCropTransform())
                .into((AppCompatImageView) findViewById(R.id.post_postimg));

        Glide.with(this)
                .load("https://s3.ap-northeast-2.amazonaws.com/quvechefbucket/profile/" + post.getUserImg())
                .apply(RequestOptions.placeholderOf(R.drawable.ic_person_black_24dp).circleCrop())
                .into((AppCompatImageView) findViewById(R.id.post_userimg));

        ((AppCompatTextView) findViewById(R.id.post_nickname)).setText(post.getNickname());
        ((AppCompatTextView) findViewById(R.id.post_likecount)).setText("1623");
        ((AppCompatTextView) findViewById(R.id.post_contents)).setText(post.getContents());
        ((AppCompatTextView) findViewById(R.id.post_time)).setText("1일전");

        findViewById(R.id.post_like).setOnClickListener(v -> DoChef.getAppInstance().showToast("좋아요"));
        findViewById(R.id.post_comment).setOnClickListener(v -> startActivity(new Intent(PostActivity.this, CommentActivity.class)));
        findViewById(R.id.post_other).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(PostActivity.this, v);
                // if(ismaster)
                PostActivity.this.getMenuInflater().inflate(R.menu.menu_post_master, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
//                                case R.id.menu_post_normal_report:
//                                    break;
                            case R.id.menu_post_master_revise:
                                Intent intent = new Intent(PostActivity.this, RevisePostActivity.class);
                                intent.putExtra("postid", post.getPostID())
                                        .putExtra("contents", post.getContents())
                                        .putExtra("postimg", "https://s3.ap-northeast-2.amazonaws.com/quvechefbucket/postImage/" + post.getPostImg());
                                startActivity(intent);
                                break;
                            case R.id.menu_post_master_delete:
                                DoChef.getAppInstance().showToast("삭제");
                                AlertDialog.Builder builder = new AlertDialog.Builder(PostActivity.this);
                                builder.setMessage("삭제하시겠습니까?")
                                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                finish();
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
            }
        });

        findViewById(R.id.post_user_layout).setOnClickListener(v -> {
            SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            try {
                if (mSharedPreferences.getBoolean(Preferences.SHAREDPREFERENCE_AUTOLOGIN, false) &&
                        post.getUserID().equals(new JSONObject(mSharedPreferences.getString(Preferences.SHAREDPREFERENCE_USERINFO, null)).getString("USER_ID"))) {

                    Intent intent = new Intent(PostActivity.this, MyHomeActivity.class);
                    startActivity(intent);

                } else {
                    Intent intent = new Intent(PostActivity.this, UserHomeActivity.class);
                    intent.putExtra("UserID", post.getUserID());
                    startActivity(intent);
                }
            } catch (JSONException e) {
                Intent intent = new Intent(PostActivity.this, UserHomeActivity.class);
                intent.putExtra("UserID", post.getUserID());
                startActivity(intent);
            }
        });

        ((FlexboxLayout) findViewById(R.id.post_tags)).removeAllViews();
        String[] tags = {"tag1", "tag2", "tag3", "tag4"};

        for (int i = 0; i < tags.length; i++) {
            AppCompatTextView textView = new AppCompatTextView(PostActivity.this);
            textView.setText("#" + tags[i] + " ");
            textView.setTextColor(getResources().getColor(R.color.colorPrimary));

            ((FlexboxLayout) findViewById(R.id.post_tags)).addView(textView);
        }


        ((FlexboxLayout) findViewById(R.id.post_commentdetail)).removeAllViews();
        List<PostComment> aa = new ArrayList<>();
        PostComment bb = new PostComment();
        bb.setNickName("유저1");
        bb.setContents("댓글1\n댓글1\n댓글1\n댓글1");
        bb.setDate(0);

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
                motherview.setOnClickListener(v -> startActivity(new Intent(PostActivity.this, CommentActivity.class)));

                ((FlexboxLayout) findViewById(R.id.post_commentdetail)).addView(motherview);
            }
        } else {
            findViewById(R.id.post_comment_layout).setVisibility(View.GONE);
        }
    }

    private interface PostActivityService {
        @GET("post/commentlist.php")
        Call<ArrayList<PostComment>> GetCommentCall(@Query("PostID") int postID);
    }

    //temp
    private interface TimeLineService {
        @GET("post/timeline.php")
        Call<ArrayList<Post>> GetPostCall(@Query("last") int last);
    }
}