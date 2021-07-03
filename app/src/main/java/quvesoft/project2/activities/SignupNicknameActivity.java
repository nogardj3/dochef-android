package quvesoft.project2.activities;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.appcompat.widget.AppCompatEditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.JsonObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import quvesoft.project2.Preferences;
import quvesoft.project2.Project2;
import quvesoft.project2.R;
import quvesoft.project2.base.BaseActivity;
import quvesoft.project2.utils.BasicCallback;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class SignupNicknameActivity extends BaseActivity {
    public static final String ACCESS_TOKEN = "AccessToken";
    @BindView(R.id.signupnick_nickname)
    AppCompatEditText editText_nickname;
    private String AccessToken;
    private boolean signupSuccess = false;
    private ProgressDialog mProgressDialog;
    private SignUpService signUpService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_signupnick);
        ButterKnife.bind(this);

        mProgressDialog = new ProgressDialog(this);
        AccessToken = getIntent().getStringExtra(ACCESS_TOKEN);

        signUpService = new Retrofit.Builder()
                .baseUrl("http://52.78.223.19/chef/")
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(SignUpService.class);
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
            Project2.getAppInstance().showToast("닉네임의 길이가 너무 짧습니다. 1자 이상 입력해주세요");
        } else if (!nickname.matches("[0-9|a-z|A-Z|ㄱ-ㅎ|ㅏ-ㅣ|가-힝]*")) {
            Project2.getAppInstance().showToast("사용할 수 없는 닉네임입니다. 숫자, 알파벳 대소문자, 한글만 사용가능합니다.");
        } else {
            mProgressDialog.show();
            signUpService.SignupCall(AccessToken, nickname)
                    .enqueue(new BasicCallback<JsonObject>(SignupNicknameActivity.this) {
                        @Override
                        public void onResponse(Response<JsonObject> response, int err) {
                            mProgressDialog.dismiss();
                            switch (err) {
                                case 822:
                                    Project2.getAppInstance().showToast("이미 존재하는 닉네임입니다.");
                                    break;
                                case 825:
                                    Project2.getAppInstance().showToast("닉네임의 길이가 너무 짧습니다. 1자 이상 입력해주세요");
                                    break;
                                case 826:
                                    Project2.getAppInstance().showToast("사용할 수 없는 닉네임입니다. 숫자, 알파벳 대소문자, 한글만 사용가능합니다.");
                                    break;
                                case 0:
                                    Project2.getAppInstance().showToast("회원가입되었습니다.");

                                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putBoolean(Preferences.SHAREDPREFERENCE_AUTOLOGIN, true);
                                    editor.putString(Preferences.SHAREDPREFERENCE_USERINFO, response.body().toString());
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

    private interface SignUpService {
        @FormUrlEncoded
        @POST("user/sign/signup.php")
        Call<JsonObject> SignupCall(@Field("token") String token, @Field("Nickname") String Nickname);
    }
}
