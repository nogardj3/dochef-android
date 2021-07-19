package com.yhjoo.dochef.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.activities.BaseActivity;
import com.yhjoo.dochef.activities.HomeActivity;
import com.yhjoo.dochef.activities.RecipeMyListActivity;
import com.yhjoo.dochef.activities.SettingActivity;
import com.yhjoo.dochef.databinding.FMainUserBinding;
import com.yhjoo.dochef.interfaces.RxRetrofitServices;
import com.yhjoo.dochef.model.UserDetail;
import com.yhjoo.dochef.utils.DataGenerator;
import com.yhjoo.dochef.utils.ImageLoadUtil;
import com.yhjoo.dochef.utils.RxRetrofitBuilder;
import com.yhjoo.dochef.utils.Utils;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;

public class MainUserFragment extends Fragment {
    FMainUserBinding binding;

    RxRetrofitServices.UserService userService;

    UserDetail userDetailInfo;
    String userID;

    /*
        TODO
    */

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FMainUserBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        userService = RxRetrofitBuilder.create(getContext(), RxRetrofitServices.UserService.class);

        userID = Utils.getUserBrief(getContext()).getUserID();

        binding.fmainUserHome.setOnClickListener(this::goHome);
        binding.fmainUserRecipe.setOnClickListener(this::goMyRecipe);
        binding.fmainUserSetting.setOnClickListener(this::goSetting);
        binding.fmainUserReview.setOnClickListener(this::goReview);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (App.isServerAlive())
            getUserDetail();
        else {
            userDetailInfo = DataGenerator.make(getResources(), getResources().getInteger(R.integer.DATA_TYPE_USER_DETAIL));

            ImageLoadUtil.loadUserImage(getContext(), userDetailInfo.getUserImg(), binding.fmainUserImg);
            binding.fmainUserNickname.setText(userDetailInfo.getNickname());
        }
    }

    void goHome(View view){
        startActivity(new Intent(getContext(), HomeActivity.class));
    }

    void goMyRecipe(View view){
        Intent intent = new Intent(getContext(), RecipeMyListActivity.class)
                .putExtra("userID", userDetailInfo.getUserID());
        startActivity(intent);
    }

    void goSetting(View view){
        startActivity(new Intent(getContext(), SettingActivity.class));
    }

    void goReview(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(
                "https://play.google.com/store/apps/details?id=quvesoft.sprout"))
                .setPackage("com.android.vending");
        try {
            startActivity(intent);
        } catch (Exception e) {
            Utils.log(e.toString());
            App.getAppInstance().showToast("스토어 열기 실패");
        }
    }

    void getUserDetail() {
        ((BaseActivity) getActivity()).getCompositeDisposable().add(
                userService.getUserDetail(userID)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                            userDetailInfo = response.body();

                            ImageLoadUtil.loadUserImage(
                                    MainUserFragment.this.getContext(), userDetailInfo.getUserImg(), binding.fmainUserImg);
                            binding.fmainUserNickname.setText(userDetailInfo.getNickname());
                        }, RxRetrofitBuilder.defaultConsumer())
        );
    }
}