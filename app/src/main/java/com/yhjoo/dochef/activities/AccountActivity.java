package com.yhjoo.dochef.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.constraintlayout.widget.Group;

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
import com.google.gson.JsonObject;
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.base.BaseActivity;
import com.yhjoo.dochef.interfaces.RetrofitServices;
import com.yhjoo.dochef.utils.BasicCallback;
import com.yhjoo.dochef.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AccountActivity extends BaseActivity {
    @BindView(R.id.account_signin_email)
    AppCompatEditText editText_signin_email;
    @BindView(R.id.account_signin_password)
    AppCompatEditText editText_signin_pw;
    @BindView(R.id.account_signup_email)
    AppCompatEditText editText_signup_email;
    @BindView(R.id.account_signup_password)
    AppCompatEditText editText_signup_pw;
    @BindView(R.id.account_signupnick_nickname)
    AppCompatEditText editText_nickname;
    @BindView(R.id.account_signin_group)
    Group group_signin;
    @BindView(R.id.account_signup_group)
    Group group_signup;
    @BindView(R.id.account_signupnick_group)
    Group group_signupnick;
    @BindView(R.id.account_findpw_group)
    Group group_findpw;

    private final int RC_SIGN_IN = 9001;

    enum Mode {SIGNIN, SIGNUP, SIGNUPNICK, FINDPW}

    private ProgressDialog mProgressDialog;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAnalytics mFirebaseAnalytics;
    private RetrofitServices.AccountService accountService;

    private String idToken;

    Mode current_mode = Mode.SIGNIN;

    /*
        TODO
        1. Signin, Signup, SignupNick, FindPW 등등 기능 구현
        2. 가르기 힘들면 Fragment 분기
        3. retrofit 구현
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_account);
        ButterKnife.bind(this);

        mProgressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);


        accountService = new Retrofit.Builder()
                .baseUrl(getString(R.string.server_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(RetrofitServices.AccountService.class);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        if (mAuth.getCurrentUser() != null)
            System.out.println(mAuth.getCurrentUser());
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
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Utils.log("Google sign in failed", e.toString());
                App.getAppInstance().showToast("구글 인증 오류. 잠시 후 다시 시도해주세요.");
                progressOFF();
            }
        } else {
            Utils.log("Something wrong?");
            progressOFF();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressOFF();
    }

    @OnClick({R.id.account_signin_ok, R.id.account_signin_google, R.id.account_signin_signup, R.id.account_signin_findpw,
            R.id.account_signup_ok,R.id.account_signupnick_ok})
    void oc(View v) {
        switch (v.getId()) {
            case R.id.account_signin_ok:
                String signin_email = editText_signin_email.getText().toString();
                String signin_pw = editText_signin_pw.getText().toString();

                if(Utils.emailValidation(signin_email) == Utils.EMAIL_VALIDATE.NODATA || Utils.pwValidation(signin_pw) == Utils.PW_VALIDATE.NODATA)
                    App.getAppInstance().showToast("이메일과 비밀번호를 모두 입력해주세요.");
                else if(Utils.emailValidation(signin_email) == Utils.EMAIL_VALIDATE.INVALID)
                    App.getAppInstance().showToast("이메일 형식이 올바르지 않습니다.");
                else if (Utils.pwValidation(signin_pw) == Utils.PW_VALIDATE.SHORT || Utils.pwValidation(signin_pw) == Utils.PW_VALIDATE.LONG)
                    App.getAppInstance().showToast("비밀번호 길이를 확인 해 주세요. 8자 이상, 16자 이하로 입력 해 주세요.");
                else if (Utils.pwValidation(signin_pw) == Utils.PW_VALIDATE.INVALID)
                    App.getAppInstance().showToast("비밀번호 형식을 확인 해 주세요. 숫자, 알파벳 대소문자만 사용가능합니다.");
                else{
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
                                } else
                                    checkUserToken(idToken);
                            });
                }
                break;
            case R.id.account_signin_google:
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
                break;
            case R.id.account_signin_signup:
                startMode(Mode.SIGNUP,"");
                break;
            case R.id.account_signin_findpw:
                startMode(Mode.FINDPW,"");
                break;
            case R.id.account_signup_ok:
                String email = editText_signup_email.getText().toString();
                String pw = editText_signup_pw.getText().toString();

                if(Utils.emailValidation(email) == Utils.EMAIL_VALIDATE.NODATA || Utils.pwValidation(email) == Utils.PW_VALIDATE.NODATA)
                    App.getAppInstance().showToast("이메일과 비밀번호를 모두 입력해주세요.");
                else if(Utils.emailValidation(email) == Utils.EMAIL_VALIDATE.INVALID)
                    App.getAppInstance().showToast("이메일 형식이 올바르지 않습니다.");
                else if (Utils.pwValidation(pw) == Utils.PW_VALIDATE.SHORT || Utils.pwValidation(pw) == Utils.PW_VALIDATE.LONG)
                    App.getAppInstance().showToast("비밀번호 길이를 확인 해 주세요. 8자 이상, 16자 이하로 입력 해 주세요.");
                else if (Utils.pwValidation(pw) == Utils.PW_VALIDATE.INVALID)
                    App.getAppInstance().showToast("비밀번호 형식을 확인 해 주세요. 숫자, 알파벳 대소문자만 사용가능합니다.");
                else{
                    progressON(AccountActivity.this);
                    mAuth.createUserWithEmailAndPassword(email,pw)
                            .addOnCompleteListener(authTask -> {
                                if (!authTask.isSuccessful()) {
                                    progressOFF();
                                    Exception e = authTask.getException();
                                    if (e instanceof FirebaseAuthException) {
                                        String fbae = ((FirebaseAuthException) e).getErrorCode();
                                        switch (fbae) {
                                            case "ERROR_EMAIL_ALREADY_IN_USE":
                                                App.getAppInstance().showToast("이미 가입되있는 이메일입니다.");
                                                break;
                                            default:
                                                App.getAppInstance().showToast("알 수 없는 오류 발생. 다시 시도해 주세요.");
                                                break;
                                        }
                                    } else if (e instanceof FirebaseNetworkException) {
                                        App.getAppInstance().showToast("네트워크 상태를 확인해주세요.");
                                    } else {
                                        App.getAppInstance().showToast("알 수 없는 오류가 발생. 다시 시도해 주세요");
                                    }
                                } else {
                                    authTask.getResult().getUser().getIdToken(false)
                                            .addOnCompleteListener(task -> {
                                                if (!task.isSuccessful()) {
                                                    progressOFF();
                                                    App.getAppInstance().showToast("알 수 없는 오류가 발생. 다시 시도해 주세요");
                                                } else {
                                                    final String idToken = task.getResult().getToken();
                                                    addUserToServer(idToken);
                                                }
                                            });
                                }
                            });
                }

                break;
            case R.id.account_signupnick_ok:
                String nickname = editText_nickname.getText().toString();

                if (Utils.nicknameValidate(nickname) == Utils.NICKNAME_VALIDATE.NODATA)
                    App.getAppInstance().showToast("닉네임을 입력 해 주세요.");
                else if (Utils.nicknameValidate(nickname) == Utils.NICKNAME_VALIDATE.SHORT ||
                        Utils.nicknameValidate(nickname) == Utils.NICKNAME_VALIDATE.LONG)
                    App.getAppInstance().showToast("닉네임의 길이를 확인 해 주세요. 6자 이상, 10자 이하로 입력해주세요");
                else if (Utils.nicknameValidate(nickname) == Utils.NICKNAME_VALIDATE.INVALID)
                    App.getAppInstance().showToast("사용할 수 없는 닉네임입니다. 숫자, 알파벳 대소문자, 한글만 사용가능합니다.");
                else {
                    progressON(AccountActivity.this);
                    accountService.signUp(idToken, nickname)
                            .enqueue(new BasicCallback<JsonObject>(AccountActivity.this) {
                                @Override
                                public void onResponse(Response<JsonObject> response, int err) {
                                    progressOFF();

                                    if (err == 822)
                                        App.getAppInstance().showToast("이미 존재하는 닉네임입니다.");
                                    else {
                                        App.getAppInstance().showToast("회원가입되었습니다.");
                                        startMain(response.body().toString());
                                    }
                                }

                                @Override
                                public void onFailure(Call<JsonObject> call, Throwable t) {
                                    super.onFailure(call, t);
                                    Utils.log(t.toString());

                                    progressOFF();
                                }
                            });
                }
                break;
        }
    }


    public void startMode(AccountActivity.Mode mode, String token){
        group_signin.setVisibility(View.GONE);
        group_signup.setVisibility(View.GONE);
        group_signupnick.setVisibility(View.GONE);
        group_findpw.setVisibility(View.GONE);

        if (mode == Mode.SIGNIN){

        }
        else if (mode == Mode.SIGNUP){
            current_mode = Mode.SIGNUP;
            group_signup.setVisibility(View.VISIBLE);

            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, getString(R.string.analytics_id_signup));
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, getString(R.string.analytics_name_signup));
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, getString(R.string.analytics_type_text));
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle);
        }
        else if (mode == Mode.SIGNUPNICK){
            current_mode = Mode.SIGNUPNICK;
            group_signupnick.setVisibility(View.VISIBLE);

        }
        else if (mode == Mode.FINDPW){
            current_mode = Mode.FINDPW;
            group_findpw.setVisibility(View.VISIBLE);

        }
    }

    public void checkUserToken(String idToken){
        // TODO
        // 1. 토큰 전달해서 서버에 확인
        //      서버에 있으면 -> startMain(JSONOBJECT)
        //      서버에 없으면 -> token 써서 기본정보 서버 저장 -> 닉네임을 설정 해 주세요. startMode(Mode.SIGNUPNICK,"")
    }

    private void firebaseAuthWithGoogle(String googleToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(googleToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Utils.log("success");
                        FirebaseUser user = mAuth.getCurrentUser();

                        user.getIdToken(false)
                                .addOnCompleteListener(tt -> {
                                    if (!tt.isSuccessful()) {
                                        progressOFF();
                                        App.getAppInstance().showToast("알 수 없는 오류가 발생. 다시 시도해 주세요");
                                    } else {
                                        String idToken = tt.getResult().getToken();
                                        addUserToServer(idToken);
                                    }
                                });

                    } else{
                        Utils.log(task.getException().toString());
                        App.getAppInstance().showToast("알 수 없는 오류 발생. 다시 시도해 주세요.");
                    }
                });
    }



    public void addUserToServer(String token){
        accountService
                .checkToken(token)
                .enqueue(new BasicCallback<JsonObject>(AccountActivity.this) {
                    @Override
                    public void onResponse(Response<JsonObject> response, int err) {
                        progressOFF();
                        switch (err) {
                            // 가입 정보 없음
                            case 814:
                                startMode(AccountActivity.Mode.SIGNUPNICK, token);
                                break;
                            // 가입 정보 있음
                            case 0:
                                startMain(response.body().toString());
                                break;
                        }
                    }

                    @Override
                    public void onFailure() {
                        progressOFF();
                    }
                });
    }

    public void startMain(String userinfo){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(getString(R.string.SP_ACTIVATEDDEVICE), true);
        editor.putString(getString(R.string.SP_USERINFO), userinfo);
        editor.apply();

        finish();
    }
}