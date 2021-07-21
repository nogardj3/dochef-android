package com.yhjoo.dochef.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.databinding.AAccountBinding;
import com.yhjoo.dochef.interfaces.RxRetrofitServices;
import com.yhjoo.dochef.model.UserBrief;
import com.yhjoo.dochef.utils.RxRetrofitBuilder;
import com.yhjoo.dochef.utils.Utils;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import retrofit2.HttpException;

public class AccountActivity extends BaseActivity {
    private final int RC_SIGN_IN = 9001;

    enum Mode {SIGNIN, SIGNUP, SIGNUPNICK, FINDPW}

    AAccountBinding binding;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAnalytics mFirebaseAnalytics;
    FirebaseAuth mAuth;
    RxRetrofitServices.AccountService accountService;

    Mode current_mode = Mode.SIGNIN;
    String idToken;
    String fcmToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Utils.log(task.getException());
                        return;
                    }

                    String token = task.getResult();
                    Utils.log(token);
                    fcmToken = token;
                });

        accountService = RxRetrofitBuilder.create(this, RxRetrofitServices.AccountService.class);

        binding.accountSigninOk.setOnClickListener(this::signInWithEmailPW);
        binding.accountSigninGoogle.setOnClickListener(this::tryGoogleSignIn);
        binding.accountSigninSignup.setOnClickListener(v -> startMode(Mode.SIGNUP, ""));
        binding.accountSigninFindpw.setOnClickListener(v -> startMode(Mode.FINDPW, ""));
        binding.accountSignupOk.setOnClickListener(this::startSignUp);
        binding.accountSignupnickOk.setOnClickListener(this::signUpWithEmailPW);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, getString(R.string.analytics_id_signin));
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, getString(R.string.analytics_name_signin));
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, getString(R.string.analytics_type_text));
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            progressON(this);

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Utils.log("firebaseAuthWithGoogle:" + account.getId());
                signInWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Utils.log("Google sign in failed", e.toString());
                App.getAppInstance().showToast("구글 인증 오류. 잠시 후 다시 시도해주세요.");
                progressOFF();
            }
        } else {
            Utils.log("Something wrong");
            progressOFF();
        }
    }

    void signInWithEmailPW(View v) {
        String signin_email = binding.accountSigninEmail.getText().toString();
        String signin_pw = binding.accountSigninPassword.getText().toString();

        if (Utils.emailValidation(signin_email) == Utils.EMAIL_VALIDATE.NODATA
                || Utils.pwValidation(signin_pw) == Utils.PW_VALIDATE.NODATA)
            App.getAppInstance().showToast("이메일과 비밀번호를 모두 입력해주세요.");
        else if (Utils.emailValidation(signin_email) == Utils.EMAIL_VALIDATE.INVALID)
            App.getAppInstance().showToast("이메일 형식이 올바르지 않습니다.");
        else if (Utils.pwValidation(signin_pw) == Utils.PW_VALIDATE.SHORT
                || Utils.pwValidation(signin_pw) == Utils.PW_VALIDATE.LONG)
            App.getAppInstance().showToast("비밀번호 길이를 확인 해 주세요. 8자 이상, 16자 이하로 입력 해 주세요.");
        else if (Utils.pwValidation(signin_pw) == Utils.PW_VALIDATE.INVALID)
            App.getAppInstance().showToast("비밀번호 형식을 확인 해 주세요. 숫자, 알파벳 대소문자만 사용가능합니다.");
        else {
            mAuth.signInWithEmailAndPassword(signin_email, signin_pw)
                    .addOnCompleteListener(authTask -> {
                        if (!authTask.isSuccessful()) {
                            Exception e = authTask.getException();
                            if (e instanceof FirebaseAuthException) {
                                String fbae = ((FirebaseAuthException) e).getErrorCode();
                                switch (fbae) {
                                    case "ERROR_USER_NOT_FOUND":
                                        App.getAppInstance().showToast("존재하지 않는 이메일입니다. 가입 후 사용해 주세요.");
                                        break;
                                    case "ERROR_WRONG_PASSWORD":
                                        App.getAppInstance().showToast("비밀번호가 올바르지 않습니다.");
                                        break;
                                    case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                                        App.getAppInstance().showToast("해당 이메일주소와 연결된 다른 계정이 이미 존재합니다. 해당 이메일주소와 연결된 다른 계정을 사용하여 로그인하십시오.");
                                        break;
                                    default:
                                        App.getAppInstance().showToast("알 수 없는 오류 발생. 다시 시도해 주세요.");
                                        break;
                                }
                            } else
                                App.getAppInstance().showToast("알 수 없는 오류 발생. 다시 시도해 주세요.");
                            progressOFF();
                        } else {
                            authTask.getResult().getUser().getIdToken(true)
                                    .addOnCompleteListener(task -> {
                                        if (!task.isSuccessful()) {
                                            progressOFF();
                                            App.getAppInstance().showToast("알 수 없는 오류 발생. 다시 시도해 주세요");
                                        } else {
                                            idToken = task.getResult().getToken();
                                            checkUserInfo(idToken);
                                        }
                                    });
                        }
                    });
        }
    }

    void tryGoogleSignIn(View v) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    void signInWithGoogle(String googleToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(googleToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Utils.log("success");
                        FirebaseUser user = mAuth.getCurrentUser();

                        user.getIdToken(true)
                                .addOnCompleteListener(tt -> {
                                    if (!tt.isSuccessful()) {
                                        progressOFF();
                                        App.getAppInstance().showToast("알 수 없는 오류가 발생. 다시 시도해 주세요");
                                    } else {
                                        idToken = tt.getResult().getToken();
                                        checkUserInfo(idToken);
                                    }
                                });

                    } else {
                        Utils.log(task.getException().toString());
                        App.getAppInstance().showToast("알 수 없는 오류 발생. 다시 시도해 주세요.");
                    }
                });
    }

    void startSignUp(View v) {
        String email = binding.accountSignupEmail.getText().toString();
        String pw = binding.accountSignupPassword.getText().toString();

        if (Utils.emailValidation(email) == Utils.EMAIL_VALIDATE.NODATA || Utils.pwValidation(email) == Utils.PW_VALIDATE.NODATA)
            App.getAppInstance().showToast("이메일과 비밀번호를 모두 입력해주세요.");
        else if (Utils.emailValidation(email) == Utils.EMAIL_VALIDATE.INVALID)
            App.getAppInstance().showToast("이메일 형식이 올바르지 않습니다.");
        else if (Utils.pwValidation(pw) == Utils.PW_VALIDATE.SHORT || Utils.pwValidation(pw) == Utils.PW_VALIDATE.LONG)
            App.getAppInstance().showToast("비밀번호 길이를 확인 해 주세요. 8자 이상, 16자 이하로 입력 해 주세요.");
        else if (Utils.pwValidation(pw) == Utils.PW_VALIDATE.INVALID)
            App.getAppInstance().showToast("비밀번호 형식을 확인 해 주세요. 숫자, 알파벳 대소문자만 사용가능합니다.");
        else {
            progressON(this);
            mAuth.createUserWithEmailAndPassword(email, pw)
                    .addOnCompleteListener(authTask -> {
                        if (!authTask.isSuccessful()) {
                            progressOFF();
                            Exception e = authTask.getException();
                            if (e instanceof FirebaseAuthException) {
                                String fbae = ((FirebaseAuthException) e).getErrorCode();
                                if ("ERROR_EMAIL_ALREADY_IN_USE".equals(fbae))
                                    App.getAppInstance().showToast("이미 가입되있는 이메일입니다.");
                                else
                                    App.getAppInstance().showToast("알 수 없는 오류 발생. 다시 시도해 주세요.");
                            } else if (e instanceof FirebaseNetworkException) {
                                App.getAppInstance().showToast("네트워크 상태를 확인해주세요.");
                            } else {
                                App.getAppInstance().showToast("알 수 없는 오류가 발생. 다시 시도해 주세요");
                            }
                        } else {
                            authTask.getResult().getUser().getIdToken(true)
                                    .addOnCompleteListener(task -> {
                                        if (!task.isSuccessful()) {
                                            progressOFF();
                                            App.getAppInstance().showToast("알 수 없는 오류가 발생. 다시 시도해 주세요");
                                        } else {
                                            idToken = task.getResult().getToken();
                                            checkUserInfo(idToken);
                                        }
                                    });
                        }
                    });
        }
    }

    void signUpWithEmailPW(View v) {
        String nickname = binding.accountSignupnickNickname.getText().toString();

        if (Utils.nicknameValidate(nickname) == Utils.NICKNAME_VALIDATE.NODATA)
            App.getAppInstance().showToast("닉네임을 입력 해 주세요.");
        else if (Utils.nicknameValidate(nickname) == Utils.NICKNAME_VALIDATE.SHORT ||
                Utils.nicknameValidate(nickname) == Utils.NICKNAME_VALIDATE.LONG)
            App.getAppInstance().showToast("닉네임의 길이를 확인 해 주세요. 6자 이상, 10자 이하로 입력해주세요");
        else if (Utils.nicknameValidate(nickname) == Utils.NICKNAME_VALIDATE.INVALID)
            App.getAppInstance().showToast("사용할 수 없는 닉네임입니다. 숫자, 알파벳 대소문자, 한글만 사용가능합니다.");
        else {
            progressON(this);

            compositeDisposable.add(
                    accountService.createUser(idToken, mAuth.getUid(), nickname)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(response -> {
                                App.getAppInstance().showToast("회원 가입 되었습니다.");
                                startMain(response.body());
                            }, throwable -> {
                                throwable.printStackTrace();
                                if(throwable instanceof HttpException){
                                    int code = ((HttpException) throwable).code();

                                    if (code == 403)
                                        App.getAppInstance().showToast("이미 존재하는 닉네임입니다.");
                                }

                                progressOFF();
                            })
            );

        }
    }

    void checkUserInfo(String idToken) {
        compositeDisposable.add(
                accountService.checkUser(idToken, mAuth.getUid(),fcmToken)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response -> {
                        startMain(response.body());
                    }, e -> {
                        e.printStackTrace();
                        if(e instanceof HttpException){
                            int code = ((HttpException) e).code();

                            if (code == 404) {
                                App.getAppInstance().showToast("닉네임을 입력해주세요.");
                                startMode(AccountActivity.Mode.SIGNUPNICK, idToken);
                            }
                        }

                        progressOFF();
                    })
        );
    }

    void startMode(AccountActivity.Mode mode, String token) {
        binding.accountSigninGroup.setVisibility(View.GONE);
        binding.accountSignupGroup.setVisibility(View.GONE);
        binding.accountSignupnickGroup.setVisibility(View.GONE);
        binding.accountFindpwGroup.setVisibility(View.GONE);

        if (mode == Mode.SIGNUP) {
            current_mode = Mode.SIGNUP;
            binding.accountSignupGroup.setVisibility(View.VISIBLE);

            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, getString(R.string.analytics_id_signup));
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, getString(R.string.analytics_name_signup));
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, getString(R.string.analytics_type_text));
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle);
        } else if (mode == Mode.SIGNUPNICK) {
            current_mode = Mode.SIGNUPNICK;
            binding.accountSignupnickGroup.setVisibility(View.VISIBLE);

        } else if (mode == Mode.FINDPW) {
            current_mode = Mode.FINDPW;
            binding.accountFindpwGroup.setVisibility(View.VISIBLE);
        }
    }

    void startMain(UserBrief userinfo) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        editor.putBoolean(getString(R.string.SP_ACTIVATEDDEVICE), true);
        editor.putString(getString(R.string.SP_USERINFO), gson.toJson(userinfo));
        editor.apply();

        Utils.log(userinfo.toString());
        Utils.log(gson.toJson(userinfo));

        startActivity(new Intent(this, MainActivity.class));

        finish();
    }
}