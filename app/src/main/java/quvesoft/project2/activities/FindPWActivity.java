package quvesoft.project2.activities;

import android.os.Bundle;

import java.util.concurrent.TimeUnit;

import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import quvesoft.project2.R;
import quvesoft.project2.base.BaseActivity;

public class FindPWActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_findpw);
    }

    @OnClick(R.id.findpw_ok)
    void oc() {
        progressON(this);
        Observable.timer(2, TimeUnit.SECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(count -> {
                    progressOFF();
                    finish();
                });
    }
}
