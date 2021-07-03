package com.yhjoo.dochef.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.widget.AppCompatEditText;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.gson.JsonObject;
import com.yhjoo.dochef.DoChef;
import com.yhjoo.dochef.Preferences;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.base.BaseActivity;
import com.yhjoo.dochef.utils.BasicCallback;
import com.yhjoo.dochef.utils.ChefAuth;
import com.yhjoo.dochef.utils.Util;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class LoginActivity extends BaseActivity {
    private final int RC_SIGN_IN = 6006;
    @BindView(R.id.login_email)
    AppCompatEditText editText_id;
    @BindView(R.id.login_password)
    AppCompatEditText editText_pw;

    private ProgressDialog mProgressDialog;
    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInClient mGoogleSignInClient;

    private LoginService loginService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_login);
        ButterKnife.bind(this);

        mProgressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

//        deprecated methods
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .enableAutoManage(this, connectionResult -> Log.w("login_error", "google1"))
//                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
//                .build();
//        new
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        loginService = new Retrofit.Builder()
                .baseUrl(getString(R.string.server_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(LoginService.class);

        if (mAuth.getCurrentUser() != null) {
            mAuth.getCurrentUser().getIdToken(true)
                    .addOnCompleteListener(this::authWithServer);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN){
            progressON(this);

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Util.log("firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Util.log("Google sign in failed", e.toString());
                DoChef.getAppInstance().showToast("구글 인증 오류. 잠시 후 다시 시도해주세요.");
                mProgressDialog.dismiss();
            }
        }
        else{
            Util.log("Something wrong?");
            mProgressDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressOFF();
    }

    @OnClick({R.id.login_ok, R.id.login_signup, R.id.login_findpw, R.id.login_google})
    void oc(View v) {
        switch (v.getId()) {
            case R.id.login_ok:
//              TODO textwatcher로 바꾸기
                if (mAuth.getCurrentUser() != null) {
                    //DoChef.getAppInstance().showToast(getString(R.string.signin_err_wronginput));
                } else if (editText_id.getText().length() > 0 && editText_pw.length() > 0) {

                    if (!isValidEmail(editText_id.getText())) {
                        DoChef.getAppInstance().showToast("이메일 형식이 올바르지 않습니다.");
                    } else if (editText_pw.getText().length() < 6) {
                        DoChef.getAppInstance().showToast("비밀번호를 6자 이상 입력해주세요.");
                    } else {
                        mProgressDialog.show();

                        mAuth.signInWithEmailAndPassword(editText_id.getText().toString(), editText_pw.getText().toString())
                                .addOnCompleteListener(this::authResultChecker);
                    }

                } else {
                    DoChef.getAppInstance().showToast("이메일과 비밀번호를 모두 입력해주세요.");
                }
                break;
            case R.id.login_signup:
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
                break;
            case R.id.login_findpw:
                startActivity(new Intent(LoginActivity.this, FindPWActivity.class));
                break;
            case R.id.login_google:
//                deprecated
//                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);

                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
                break;
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        mAuth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))
                .addOnCompleteListener(this::authResultChecker);
    }

    private void authResultChecker(Task<AuthResult> authTask) {
        if (!authTask.isSuccessful()) {
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
                    case "ERROR_USER_NOT_FOUND":
                        DoChef.getAppInstance().showToast("존재하지 않는 이메일입니다.");
                        break;
                    case "ERROR_WRONG_PASSWORD":
                        DoChef.getAppInstance().showToast("비밀번호가 올바르지 않습니다.");
                        break;
                    case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                        DoChef.getAppInstance().showToast("해당 이메일주소와 연결된 다른 계정이 이미 존재합니다. 해당 이메일주소와 연결된 다른 계정을 사용하여 로그인하십시오.");
                        break;
                    default:
                        DoChef.getAppInstance().showToast("알 수 없는 오류 발생. 다시 시도해 주세요.");
                        break;
                }
            } else if (e instanceof FirebaseNetworkException) {
                DoChef.getAppInstance().showToast("네트워크 상태를 확인해주세요.");
            } else {
                DoChef.getAppInstance().showToast("알 수 없는 오류 발생. 다시 시도해 주세요.");
            }
            mProgressDialog.dismiss();
        } else {

            authTask.getResult().getUser().getIdToken(true)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            authWithServer(task);
                        } else {
                            ChefAuth.LogOut(LoginActivity.this);
                            mProgressDialog.dismiss();
                        }
                    });
        }
    }

    private void authWithServer(Task<GetTokenResult> task) {
        if (task.isSuccessful()) {
            final String idToken = task.getResult().getToken();

            loginService.CheckTokenCall(idToken)
                    .enqueue(new BasicCallback<JsonObject>(LoginActivity.this) {
                        @Override
                        public void onResponse(Response<JsonObject> response, int err) {
                            switch (err) {
                                case 814:
                                    Intent intent = new Intent(LoginActivity.this, SignupNicknameActivity.class)
                                            .putExtra(SignupNicknameActivity.ACCESS_TOKEN, idToken);
                                    startActivity(intent);
                                    mProgressDialog.dismiss();
                                    finish();
                                    break;
                                case 0:
                                    DoChef.getAppInstance().showToast("로그인되었습니다.");

                                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putBoolean(Preferences.SHAREDPREFERENCE_AUTOLOGIN, true)
                                            .putString(Preferences.SHAREDPREFERENCE_USERINFO, response.body().toString())
                                            .apply();

                                    mProgressDialog.dismiss();
                                    finish();
                                    break;
                            }
                        }

                        @Override
                        public void onFailure() {
                            ChefAuth.LogOut(LoginActivity.this);
                            mProgressDialog.dismiss();
                        }
                    });
        } else {
            ChefAuth.LogOut(LoginActivity.this);
            DoChef.getAppInstance().showToast("알 수 없는 오류 발생. 다시 시도해 주세요.");
            mProgressDialog.dismiss();
        }
    }

    private boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private interface LoginService {
        @FormUrlEncoded
        @POST("user/token/check.php")
        Call<JsonObject> CheckTokenCall(@Field("token") String token);
    }
}