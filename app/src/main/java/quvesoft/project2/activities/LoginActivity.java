package quvesoft.project2.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.appcompat.widget.AppCompatEditText;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.gson.JsonObject;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import quvesoft.project2.Preferences;
import quvesoft.project2.Project2;
import quvesoft.project2.R;
import quvesoft.project2.base.BaseActivity;
import quvesoft.project2.utils.BasicCallback;
import quvesoft.project2.utils.ChefAuth;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class LoginActivity extends BaseActivity {
    private interface LoginService {
        @FormUrlEncoded
        @POST("user/token/check.php")
        Call<JsonObject> CheckTokenCall(@Field("token") String token);
    }

    @BindView(R.id.login_email)
    AppCompatEditText editText_id;
    @BindView(R.id.login_password)
    AppCompatEditText editText_pw;

    private ProgressDialog mProgressDialog;
    private FirebaseAuth mAuth;
    private CallbackManager facebookCallbackManager;
    private GoogleApiClient mGoogleApiClient;

    private LoginService loginService;

    private final int RC_SIGN_IN = 6006;

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

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, connectionResult -> Log.w("login_error", "google1"))
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        loginService = new Retrofit.Builder()
                .baseUrl("http://52.78.223.19/chef/")
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(LoginService.class);

        if (mAuth.getCurrentUser() != null) {
            mAuth.getCurrentUser().getIdToken(true)
                    .addOnCompleteListener(this::authWithSproutServer);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RC_SIGN_IN:
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                progressON(this);
                if (result.isSuccess()) {
                    GoogleSignInAccount account = result.getSignInAccount();
                    firebaseAuthWithGoogle(account);
                } else {
                    Project2.getAppInstance().showToast("구글 인증 오류. 잠시 후 다시 시도해주세요.");
                    mProgressDialog.dismiss();
                }
                break;
            case Preferences.FACEBOOK_REQUEST_CODE:
                facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
                break;
            default:
                mProgressDialog.dismiss();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressOFF();
    }

    @OnClick({R.id.login_ok, R.id.login_signup, R.id.login_findpw, R.id.login_facebook, R.id.login_google})
    void oc(View v) {
        switch (v.getId()) {
            case R.id.login_ok:
                if (mAuth.getCurrentUser() != null) {
                    //Project2.getAppInstance().showToast(getString(R.string.signin_err_wronginput));
                } else if (editText_id.getText().length() > 0 && editText_pw.length() > 0) {

                    if (!isValidEmail(editText_id.getText())) {
                        Project2.getAppInstance().showToast("이메일 형식이 올바르지 않습니다.");
                    } else if (editText_pw.getText().length() < 6) {
                        Project2.getAppInstance().showToast("비밀번호를 6자 이상 입력해주세요.");
                    } else {
                        mProgressDialog.show();

                        mAuth.signInWithEmailAndPassword(editText_id.getText().toString(), editText_pw.getText().toString())
                                .addOnCompleteListener(this::authResultChecker);
                    }

                } else {
                    Project2.getAppInstance().showToast("이메일과 비밀번호를 모두 입력해주세요.");
                }
                break;
            case R.id.login_signup:
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
                break;
            case R.id.login_findpw:
                startActivity(new Intent(LoginActivity.this, FindPWActivity.class));
                break;
            case R.id.login_facebook:
                facebookCallbackManager = CallbackManager.Factory.create();

                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile", "email"));
                LoginManager.getInstance().registerCallback(facebookCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        if (AccessToken.getCurrentAccessToken() != null) {
                            handleFacebookAccessToken(AccessToken.getCurrentAccessToken());
                        } else {
                            FirebaseAuth.getInstance().signOut();
                            mProgressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Project2.getAppInstance().showToast("페이스북 인증 실패. 잠시 후 다시 시도해주세요.");
                        mProgressDialog.dismiss();
                    }
                });
                break;
            case R.id.login_google:
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
                break;
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {
        mAuth.signInWithCredential(FacebookAuthProvider.getCredential(token.getToken()))
                .addOnCompleteListener(this::authResultChecker);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        mAuth.signInWithCredential(GoogleAuthProvider.getCredential(acct.getIdToken(), null))
                .addOnCompleteListener(this::authResultChecker);
    }

    private void authResultChecker(Task<AuthResult> authTask) {
        if (!authTask.isSuccessful()) {
            Exception e = authTask.getException();
            if (e instanceof FirebaseAuthException) {
                String fbae = ((FirebaseAuthException) e).getErrorCode();
                switch (fbae) {
                    case "ERROR_INVALID_EMAIL":
                        Project2.getAppInstance().showToast("이메일 형식이 올바르지 않습니다.");
                        break;
                    case "ERROR_WEAK_PASSWORD":
                        Project2.getAppInstance().showToast("비밀번호를 6자 이상 입력해주세요.");
                        break;
                    case "ERROR_USER_NOT_FOUND":
                        Project2.getAppInstance().showToast("존재하지 않는 이메일입니다.");
                        break;
                    case "ERROR_WRONG_PASSWORD":
                        Project2.getAppInstance().showToast("비밀번호가 올바르지 않습니다.");
                        break;
                    case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                        Project2.getAppInstance().showToast("해당 이메일주소와 연결된 다른 계정이 이미 존재합니다. 해당 이메일주소와 연결된 다른 계정을 사용하여 로그인하십시오.");
                        break;
                    default:
                        Project2.getAppInstance().showToast("알 수 없는 오류 발생. 다시 시도해 주세요.");
                        break;
                }
            } else if (e instanceof FirebaseNetworkException) {
                Project2.getAppInstance().showToast("네트워크 상태를 확인해주세요.");
            } else {
                Project2.getAppInstance().showToast("알 수 없는 오류 발생. 다시 시도해 주세요.");
            }
            mProgressDialog.dismiss();
        } else {

            authTask.getResult().getUser().getIdToken(true)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            authWithSproutServer(task);
                        } else {
                            ChefAuth.LogOut(LoginActivity.this);
                            mProgressDialog.dismiss();
                        }
                    });
        }
    }

    private void authWithSproutServer(Task<GetTokenResult> task) {
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
                                    Project2.getAppInstance().showToast("로그인되었습니다.");

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
            Project2.getAppInstance().showToast("알 수 없는 오류 발생. 다시 시도해 주세요.");
            mProgressDialog.dismiss();
        }
    }

    private boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}