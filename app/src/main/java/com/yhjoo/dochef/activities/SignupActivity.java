package com.yhjoo.dochef.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.GetTokenResult;
import com.google.gson.JsonObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.yhjoo.dochef.Preferences;
import com.yhjoo.dochef.DoChef;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.base.BaseActivity;
import com.yhjoo.dochef.utils.BasicCallback;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class SignupActivity extends BaseActivity {
    @BindView(R.id.signup_email)
    AppCompatEditText editText_email;
    @BindView(R.id.signup_password)
    AppCompatEditText editText_pw;

    private ProgressDialog mProgressDialog;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_signup);
        ButterKnife.bind(this);

        mProgressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
    }

    @OnClick(R.id.signup_ok)
    void oc() {
        if (editText_email.getText().length() == 0 && editText_pw.getText().length() == 0) {
            DoChef.getAppInstance().showToast("이메일과 비밀번호를 모두 입력해주세요.");
        } else if (!isValidEmail(editText_email.getText())) {
            DoChef.getAppInstance().showToast("이메일 형식이 올바르지 않습니다.");
        } else if (editText_pw.getText().length() < 6) {
            DoChef.getAppInstance().showToast("비밀번호를 6자 이상 입력해주세요.");
        } else {
            mProgressDialog.show();
            mAuth.createUserWithEmailAndPassword(editText_email.getText().toString(), editText_pw.getText().toString())
                    .addOnCompleteListener(authTask -> {
                        if (!authTask.isSuccessful()) {
                            mProgressDialog.dismiss();
                            Exception e = authTask.getException();
                            if (e instanceof FirebaseAuthException) {
                                String fbae = ((FirebaseAuthException) e).getErrorCode();
                                switch (fbae) {
                                    case "ERROR_INVALID_EMAIL":
                                        DoChef.getAppInstance().showToast("이메일 형식이 올바르지 않습니다.");
                                        break;
                                    case "ERROR_WEAK_PASSWORD":
                                        DoChef.getAppInstance().showToast("비밀번호를 6자 이상 입력해주세요.");
                                        break;
                                    case "ERROR_EMAIL_ALREADY_IN_USE":
                                        DoChef.getAppInstance().showToast("이미 가입되있는 이메일입니다.");
                                        break;
                                    default:
                                        DoChef.getAppInstance().showToast("알 수 없는 오류 발생. 다시 시도해 주세요.");
                                        break;
                                }
                            } else if (e instanceof FirebaseNetworkException) {
                                DoChef.getAppInstance().showToast("네트워크 상태를 확인해주세요.");
                            } else {
                                DoChef.getAppInstance().showToast("알 수 없는 오류가 발생. 다시 시도해 주세요");
                            }
                        } else {
                            authTask.getResult().getUser().getIdToken(true)
                                    .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                                            if (task.isSuccessful()) {
                                                final String idToken = task.getResult().getToken();

                                                SignUpService signUpService = new Retrofit.Builder()
                                                        .baseUrl(getString(R.string.server_url))
                                                        .addConverterFactory(GsonConverterFactory.create())
                                                        .build().create(SignUpService.class);

                                                signUpService
                                                        .CheckTokenCall(idToken)
                                                        .enqueue(new BasicCallback<JsonObject>(SignupActivity.this) {
                                                            @Override
                                                            public void onResponse(Response<JsonObject> response, int err) {
                                                                mProgressDialog.dismiss();
                                                                switch (err) {
                                                                    case 814:
                                                                        Intent intent = new Intent(SignupActivity.this, SignupNicknameActivity.class)
                                                                                .putExtra(SignupNicknameActivity.ACCESS_TOKEN, idToken);
                                                                        startActivity(intent);
                                                                        finish();
                                                                        break;
                                                                    case 0:
                                                                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                                                        editor.putBoolean(Preferences.SHAREDPREFERENCE_AUTOLOGIN, true);
                                                                        editor.putString(Preferences.SHAREDPREFERENCE_USERINFO, response.body().toString());
                                                                        editor.apply();

                                                                        finish();
                                                                        break;
                                                                }
                                                            }

                                                            @Override
                                                            public void onFailure() {
                                                                mProgressDialog.dismiss();
                                                            }
                                                        });
                                            } else {
                                                mProgressDialog.dismiss();
                                                DoChef.getAppInstance().showToast("알 수 없는 오류가 발생. 다시 시도해 주세요");
                                            }
                                        }
                                    });
                        }
                    });
        }
    }

    //TODO text watcher로 바꾸기
    private boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private interface SignUpService {
        @FormUrlEncoded
        @POST("user/token/check.php")
        Call<JsonObject> CheckTokenCall(@Field("token") String token);
    }
}
