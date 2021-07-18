package com.yhjoo.dochef.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.activities.HomeActivity;
import com.yhjoo.dochef.activities.MainActivity;
import com.yhjoo.dochef.activities.RecipeDetailActivity;
import com.yhjoo.dochef.activities.RecipeMyListActivity;
import com.yhjoo.dochef.activities.RecipeThemeActivity;
import com.yhjoo.dochef.activities.SettingActivity;
import com.yhjoo.dochef.adapter.MainAdPagerAdapter;
import com.yhjoo.dochef.adapter.RecipeHorizontalAdapter;
import com.yhjoo.dochef.databinding.FMainInitBinding;
import com.yhjoo.dochef.databinding.FMainUserBinding;
import com.yhjoo.dochef.interfaces.RetrofitServices;
import com.yhjoo.dochef.model.Recipe;
import com.yhjoo.dochef.model.UserDetail;
import com.yhjoo.dochef.utils.BasicCallback;
import com.yhjoo.dochef.utils.DataGenerator;
import com.yhjoo.dochef.utils.ImageLoadUtil;
import com.yhjoo.dochef.utils.RetrofitBuilder;
import com.yhjoo.dochef.utils.Utils;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.Call;
import retrofit2.Response;

public class MainUserFragment extends Fragment {
    FMainUserBinding binding;

    RetrofitServices.UserService userService;

    UserDetail userDetailInfo;
    String userID;

    /*
        TODO
    */

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FMainUserBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        userService = RetrofitBuilder.create(getContext(), RetrofitServices.UserService.class);

        userID = Utils.getUserBrief(getContext()).getUserID();

        binding.fmainUserHome.setOnClickListener(this::goHome);
        binding.fmainUserRecipe.setOnClickListener(this::goMyRecipe);
        binding.fmainUserSetting.setOnClickListener(this::goSetting);
        binding.fmainUserReview.setOnClickListener(this::goReview);

        if (App.isServerAlive()) {
            getUserDetail();
        } else {
            userDetailInfo = DataGenerator.make(getResources(), getResources().getInteger(R.integer.DATA_TYPE_USER_DETAIL));

            ImageLoadUtil.loadUserImage(getContext(), userDetailInfo.getUserImg(), binding.fmainUserImg);

            binding.fmainUserNickname.setText(userDetailInfo.getNickname());
        }

        return view;
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
        userService.getUserDetail(userID)
                .enqueue(new BasicCallback<UserDetail>(getContext()) {
                    @Override
                    public void onResponse(Response<UserDetail> response) {
                        userDetailInfo = response.body();

                        ImageLoadUtil.loadUserImage(
                                MainUserFragment.this.getContext(), userDetailInfo.getUserImg(), binding.fmainUserImg);

                        binding.fmainUserNickname.setText(userDetailInfo.getNickname());
                    }
                });
    }
}