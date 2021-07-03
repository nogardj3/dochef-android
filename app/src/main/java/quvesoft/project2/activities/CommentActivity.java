package quvesoft.project2.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
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

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import quvesoft.project2.Project2;
import quvesoft.project2.R;
import quvesoft.project2.base.BaseActivity;
import quvesoft.project2.classes.Comment;

public class CommentActivity extends BaseActivity {
    @BindView(R.id.comment_recycler)
    RecyclerView recyclerView;
    @BindView(R.id.footer_comment_edittext)
    AppCompatEditText editText;
    @BindView(R.id.footer_comment_clear)
    AppCompatImageView clearimageview;

    private CommentListAdapter commentListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_comment);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.comment_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        commentListAdapter = new CommentListAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(commentListAdapter);

        commentListAdapter.setEmptyView(R.layout.rv_loading, (ViewGroup) recyclerView.getParent());
        commentListAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter baseQuickAdapter, View view, int position) {
                PopupMenu popup = new PopupMenu(CommentActivity.this, view);
//                    if(ismaster)
                CommentActivity.this.getMenuInflater().inflate(R.menu.menu_comment_master, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
//                                case R.id.menu_comment_user_revise:
//                                    break;
//                                case R.id.menu_comment_user_delete:
//                                    break;

                            case R.id.menu_comment_master_delete:
                                AlertDialog.Builder builder = new AlertDialog.Builder(CommentActivity.this);
                                builder.setMessage("삭제하시겠습니까?")
                                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                baseQuickAdapter.getData().remove(position);
                                                baseQuickAdapter.notifyItemRemoved(position);
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


        ArrayList<Comment> comments = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            comments.add(new Comment("유져" + i, "내용" + i + "\n", "1일전"));
            comments.add(new Comment("유져" + i, "내용" + i + "\n", "1일전"));
            comments.add(new Comment("유져" + i, "내용" + i + "\n", "1일전"));
            comments.add(new Comment("유져" + i, "내용" + i + "\n", "1일전"));
            comments.add(new Comment("유져" + i, "내용" + i + "\n", "1일전"));
            comments.add(new Comment("유져" + i, "내용" + i + "\n", "1일전"));
        }

        commentListAdapter.setNewData(comments);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                clearimageview.setVisibility(count == 0 ? View.GONE : View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @OnClick({R.id.footer_comment_ok, R.id.footer_comment_clear})
    void aa(View v) {
        switch (v.getId()) {
            case R.id.footer_comment_clear:
                editText.setText("");
                break;
            case R.id.footer_comment_ok:
                if (!editText.getText().toString().equals("")) {
                    commentListAdapter.addData(new Comment("나", editText.getText().toString(), "방금"));
                    commentListAdapter.notifyItemInserted(commentListAdapter.getData().size() - 1);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                    recyclerView.getLayoutManager().scrollToPosition(commentListAdapter.getData().size() - 1);
                    editText.setText("");
                } else {
                    Project2.getAppInstance().showToast("댓글을 입력 해 주세요");
                }
                break;
        }
    }

    private class CommentListAdapter extends BaseQuickAdapter<Comment, BaseViewHolder> {
        CommentListAdapter() {
            super(R.layout.li_comment);
        }

        @Override
        protected void convert(BaseViewHolder helper, Comment item) {
            helper.setText(R.id.li_comment_nickname, item.getNickName());
            helper.setText(R.id.li_comment_contents, item.getContents());
            helper.setText(R.id.li_comment_date, item.getDate());
            helper.addOnClickListener(R.id.li_comment_other);
        }
    }
}
