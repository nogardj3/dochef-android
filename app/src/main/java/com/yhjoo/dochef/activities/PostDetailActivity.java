package com.yhjoo.dochef.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.PopupMenu;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.model.Post;
import com.yhjoo.dochef.model.PostComment;
import com.yhjoo.dochef.databinding.APostdetailBinding;
import com.yhjoo.dochef.interfaces.RetrofitServices;
import com.yhjoo.dochef.utils.BasicCallback;
import com.yhjoo.dochef.utils.RetrofitBuilder;
import com.yhjoo.dochef.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class PostDetailActivity extends BaseActivity {
    enum MODE {MY, USER}

    enum OPERATION {VIEW, REVISE}

    APostdetailBinding binding;

    Post post;

    /*
        TODO
        1. 서버 데이터 추가 및 기능 구현
        2. retrofit 구현
        3. REVISE 기능 추가
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = APostdetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.postToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RetrofitServices.TimeLineService timeLineService =
                RetrofitBuilder.create(this, RetrofitServices.TimeLineService.class);
        RetrofitServices.PostActivityService postActivityService =
                RetrofitBuilder.create(this, RetrofitServices.PostActivityService.class);

        post = (Post) getIntent().getSerializableExtra("post");
        if (post == null) {
            timeLineService.GetPostCall(0)
                    .enqueue(new BasicCallback<ArrayList<Post>>(PostDetailActivity.this) {
                        @Override
                        public void onResponse(Response<ArrayList<Post>> response) {
                            post = response.body().get(0);

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
            postActivityService.GetCommentCall(post.getPostID())
                    .enqueue(new BasicCallback<ArrayList<PostComment>>(PostDetailActivity.this) {
                        @Override
                        public void onResponse(Response<ArrayList<PostComment>> response) {
                            setheaderfooter();
                        }
                    });
        }
    }

    void setheaderfooter() {
        Glide.with(this)
                .load(getString(R.string.storage_image_url_post) + post.getPostImg())
                .apply(RequestOptions.centerCropTransform())
                .into(binding.postPostimg);

        Glide.with(this)
                .load(getString(R.string.storage_image_url_profile) + post.getUserImg())
                .apply(RequestOptions.placeholderOf(R.drawable.ic_default_profile).circleCrop())
                .into(binding.postUserimg);

        binding.postNickname.setText(post.getNickName());
        binding.postLikecount.setText("1623");
        binding.postContents.setText(post.getContents());
        binding.postTime.setText("1일전");

        binding.postLike.setOnClickListener(v -> App.getAppInstance().showToast("좋아요"));
        binding.postComment.setOnClickListener(v -> startActivity(new Intent(PostDetailActivity.this, CommentActivity.class)));
        binding.postOther.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(PostDetailActivity.this, v);
            // if(ismaster)
            PostDetailActivity.this.getMenuInflater().inflate(R.menu.menu_post_owner, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.menu_post_owner_revise:
                        Intent intent = new Intent(PostDetailActivity.this, PostWriteActivity.class);
                        intent.putExtra("postid", post.getPostID())
                                .putExtra("contents", post.getContents())
                                .putExtra("postimg", getString(R.string.storage_image_url_post) + post.getPostImg());
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

        binding.postUserGroup.setOnClickListener(v -> {
            SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            try {
                String item_userid = post.getUserID();
                String active_userid = new JSONObject(mSharedPreferences.getString(getString(R.string.SP_USERINFO), null)).getString("user_id");

                Intent intent = new Intent(PostDetailActivity.this, HomeActivity.class);
                if (item_userid.equals(active_userid))
                    intent.putExtra("MODE",HomeActivity.MODE.MY);
                else {
                    intent.putExtra("MODE",HomeActivity.MODE.USER);
                    intent.putExtra("UserID", item_userid);
                }
                startActivity(intent);
            } catch (JSONException e) {
                Utils.log(e.toString());
            }
        });

        String[] tags = {"tag1", "tag2", "tag3", "tag4"};

        for (String tag : tags) {
            AppCompatTextView textView = new AppCompatTextView(PostDetailActivity.this);
            textView.setText("#" + tag + " ");
            textView.setTextColor(getResources().getColor(R.color.colorPrimary,null));

            binding.postTags.addView(textView);
        }


        List<PostComment> aa = new ArrayList<>();
        PostComment bb = new PostComment();
        bb.setNickName("유저1");
        bb.setContents("댓글1\n댓글1\n댓글1\n댓글1");
        bb.setDateTime(0);

        aa.add(bb);
        aa.add(bb);

        if (aa.size() != 0) {
            binding.postCommentdetail.setVisibility(View.VISIBLE);
            for (int i = 0; i < aa.size(); i++) {
                LinearLayout motherview = (LinearLayout) getLayoutInflater().inflate(R.layout.li_comment_brief, null);
                AppCompatTextView view1 = ((AppCompatTextView) motherview.findViewById(R.id.li_comment_brief_nickname));
                view1.setText(aa.get(i).getNickName());
                AppCompatTextView view2 = ((AppCompatTextView) motherview.findViewById(R.id.li_comment_brief_contents));
                view2.setText(aa.get(i).getContents());
                AppCompatTextView view3 = ((AppCompatTextView) motherview.findViewById(R.id.li_comment_brief_date));
                view3.setText("1일전");
                motherview.setOnClickListener(v -> startActivity(new Intent(PostDetailActivity.this, CommentActivity.class)));

                binding.postCommentdetail.addView(motherview);
            }
        } else
            binding.postCommentdetail.setVisibility(View.GONE);
    }
}