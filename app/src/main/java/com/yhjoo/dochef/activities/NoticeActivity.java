package com.yhjoo.dochef.activities;

import android.os.Bundle;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.adapter.FAQListAdapter;
import com.yhjoo.dochef.adapter.NoticeListAdapter;
import com.yhjoo.dochef.databinding.ANoticeBinding;
import com.yhjoo.dochef.interfaces.RetrofitServices;
import com.yhjoo.dochef.interfaces.RxRetrofitServices;
import com.yhjoo.dochef.model.ExpandContents;
import com.yhjoo.dochef.model.ExpandTitle;
import com.yhjoo.dochef.model.FAQ;
import com.yhjoo.dochef.model.Notice;
import com.yhjoo.dochef.utils.BasicCallback;
import com.yhjoo.dochef.utils.DataGenerator;
import com.yhjoo.dochef.utils.RetrofitBuilder;
import com.yhjoo.dochef.utils.RxRetrofitBuilder;

import java.util.ArrayList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import retrofit2.Call;
import retrofit2.Response;

public class NoticeActivity extends BaseActivity {
    ANoticeBinding binding;

    RxRetrofitServices.BasicService basicService;
    NoticeListAdapter noticeListAdapter;

    ArrayList<MultiItemEntity> noticeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ANoticeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.noticeToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        basicService =
                RxRetrofitBuilder.create(this, RxRetrofitServices.BasicService.class);

        noticeListAdapter = new NoticeListAdapter(noticeList);
        binding.noticeRecycler.setLayoutManager(new LinearLayoutManager(this));
        binding.noticeRecycler.setAdapter(noticeListAdapter);

        if (App.isServerAlive()){
            compositeDisposable.add(
                    basicService.getNotice()
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(response -> {
                                setListItem(response.body());
                            }, Throwable::printStackTrace)
            );
        }else{
            ArrayList<Notice> response = DataGenerator.make(getResources(),
                    getResources().getInteger(R.integer.DATA_TYPE_NOTICE));
            setListItem(response);
        }
    }

    void setListItem(ArrayList<Notice> resList){
        for (Notice item : resList) {
            ExpandTitle title = new ExpandTitle(item.title);
            title.addSubItem(new ExpandContents(item.contents, 0));
            noticeList.add(title);
        }

        noticeListAdapter.setNewData(noticeList);
        noticeListAdapter.setEmptyView(R.layout.rv_empty, (ViewGroup) binding.noticeRecycler.getParent());
    }
}
