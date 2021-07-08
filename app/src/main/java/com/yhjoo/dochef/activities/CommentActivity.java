package com.yhjoo.dochef.activities;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.base.BaseActivity;
import com.yhjoo.dochef.classes.Comment;
import com.yhjoo.dochef.databinding.ACommentBinding;
import com.yhjoo.dochef.databinding.AReviewBinding;
import com.yhjoo.dochef.utils.DummyMaker;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CommentActivity extends BaseActivity {
    private final MODE current_mode = MODE.DEFAULT;

    enum MODE {DEFAULT, REVISE}

    ACommentBinding binding;

    CommentListAdapter commentListAdapter;
    final boolean is_mine = false;

    /*
        TODO
        1. is_mine -> 뷰 보이거나 기능 열거나
        2. 타임 컨버터
        3. revise 기능
        4. delete 기능
        5. retrofit 정리
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ACommentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.commentToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        commentListAdapter = new CommentListAdapter();
        commentListAdapter.setEmptyView(R.layout.rv_loading, (ViewGroup) binding.commentRecycler.getParent());
        commentListAdapter.setOnItemChildClickListener((baseQuickAdapter, view, position) -> {
            PopupMenu popup = new PopupMenu(CommentActivity.this, view);
//                    if(is_mine)
            getMenuInflater().inflate(R.menu.menu_comment_owner, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
//                    case R.id.menu_comment_user_revise:
//                        current_mode = MODE.REVISE;
//                        break;
//                    case R.id.menu_comment_user_delete:
//                        break;

                    case R.id.menu_comment_owner_delete:
                        AlertDialog.Builder builder = new AlertDialog.Builder(CommentActivity.this);
                        builder.setMessage("삭제하시겠습니까?")
                                .setPositiveButton("확인", (dialog, which) -> {
                                    dialog.dismiss();
                                    baseQuickAdapter.getData().remove(position);
                                    baseQuickAdapter.notifyItemRemoved(position);
                                })
                                .setNegativeButton("취소", (dialog, which) -> dialog.dismiss())
                                .show();
                        break;
                }
                return false;
            });
            popup.show();
        });
        binding.commentRecycler.setLayoutManager(new LinearLayoutManager(this));
        binding.commentRecycler.setAdapter(commentListAdapter);

        // SERVER DATA
        if (App.isServerAlive()) {

        }
        // DUMMY DATA
        else
            commentListAdapter.setNewData(DummyMaker.make(getResources(), getResources().getInteger(R.integer.DUMMY_TYPE_COMMENTS)));

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
        binding.footerCommentClear.setOnClickListener(this::clearText);
    }

    void writeComment(View v){
        if (!binding.footerCommentEdittext.getText().toString().equals("")) {
            commentListAdapter.notifyItemInserted(commentListAdapter.getData().size() - 1);

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(binding.footerCommentEdittext.getWindowToken(), 0);

            binding.commentRecycler.getLayoutManager().scrollToPosition(commentListAdapter.getData().size() - 1);
            binding.footerCommentEdittext.setText("");
        } else
            App.getAppInstance().showToast("댓글을 입력 해 주세요");
    }

    void clearText(View v) {
        binding.footerCommentEdittext.setText("");
    }

    class CommentListAdapter extends BaseQuickAdapter<Comment, BaseViewHolder> {
        CommentListAdapter() {
            super(R.layout.li_comment);
        }

        @Override
        protected void convert(BaseViewHolder helper, Comment item) {
            helper.setText(R.id.li_comment_nickname, item.getNickName());
            helper.setText(R.id.li_comment_contents, item.getContents());
            helper.setText(R.id.li_comment_date, item.getDateTime());
            helper.addOnClickListener(R.id.li_comment_other);
        }
    }
}
