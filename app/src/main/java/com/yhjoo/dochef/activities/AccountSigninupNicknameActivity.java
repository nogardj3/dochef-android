package com.yhjoo.dochef.activities;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.appcompat.widget.AppCompatEditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.JsonObject;
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.base.BaseActivity;
import com.yhjoo.dochef.interfaces.RetrofitServices;
import com.yhjoo.dochef.utils.BasicCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AccountSigninupNicknameActivity extends BaseActivity {
    public static final String ACCESS_TOKEN = "AccessToken";
    @BindView(R.id.signupnick_nickname)
    AppCompatEditText editText_nickname;
    private String AccessToken;
    private boolean signupSuccess = false;
    private ProgressDialog mProgressDialog;
    private RetrofitServices.SignUpService signUpService;

    /*
        TODO
        Account로 합쳐짐
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_account_signup);
        ButterKnife.bind(this);

        mProgressDialog = new ProgressDialog(this);
        AccessToken = getIntent().getStringExtra(ACCESS_TOKEN);

        signUpService = new Retrofit.Builder()
                .baseUrl(getString(R.string.server_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(RetrofitServices.SignUpService.class);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!signupSuccess)
            FirebaseAuth.getInstance().signOut();
    }

    @OnClick(R.id.signupnick_ok)
    public void signUp() {
        String nickname = editText_nickname.getText().toString();

        if (nickname.length() < 1) {
            App.getAppInstance().showToast("닉네임의 길이가 너무 짧습니다. 1자 이상 입력해주세요");
        } else if (!nickname.matches("[0-9|a-z|A-Z|ㄱ-ㅎ|ㅏ-ㅣ|가-힝]*")) {
            App.getAppInstance().showToast("사용할 수 없는 닉네임입니다. 숫자, 알파벳 대소문자, 한글만 사용가능합니다.");
        } else {
            mProgressDialog.show();
            signUpService.SignupCall(AccessToken, nickname)
                    .enqueue(new BasicCallback<JsonObject>(AccountSigninupNicknameActivity.this) {
                        @Override
                        public void onResponse(Response<JsonObject> response, int err) {
                            mProgressDialog.dismiss();
                            switch (err) {
                                case 822:
                                    App.getAppInstance().showToast("이미 존재하는 닉네임입니다.");
                                    break;
                                case 825:
                                    App.getAppInstance().showToast("닉네임의 길이가 너무 짧습니다. 1자 이상 입력해주세요");
                                    break;
                                case 826:
                                    App.getAppInstance().showToast("사용할 수 없는 닉네임입니다. 숫자, 알파벳 대소문자, 한글만 사용가능합니다.");
                                    break;
                                case 0:
                                    App.getAppInstance().showToast("회원가입되었습니다.");

                                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putBoolean(getString(R.string.SHAREDPREFERENCE_AUTOLOGIN), true);
                                    editor.putString(getString(R.string.SHAREDPREFERENCE_USERINFO), response.body().toString());
                                    editor.apply();

                                    signupSuccess = true;
                                    finish();
                                    break;
                            }
                        }

                        @Override
                        public void onFailure() {
                            mProgressDialog.dismiss();
                        }
                    });
        }
    }

}
