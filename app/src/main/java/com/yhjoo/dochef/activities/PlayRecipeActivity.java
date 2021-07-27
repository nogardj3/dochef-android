package com.yhjoo.dochef.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.adapter.RecipeViewPagerAdapter;
import com.yhjoo.dochef.databinding.APlayrecipeBinding;
import com.yhjoo.dochef.fragments.PlayRecipeEndFragment;
import com.yhjoo.dochef.fragments.PlayRecipeItemFragment;
import com.yhjoo.dochef.fragments.PlayRecipeStartFragment;
import com.yhjoo.dochef.interfaces.RxRetrofitServices;
import com.yhjoo.dochef.model.RecipeDetail;
import com.yhjoo.dochef.model.RecipePhase;
import com.yhjoo.dochef.utils.DataGenerator;
import com.yhjoo.dochef.utils.RxRetrofitBuilder;
import com.yhjoo.dochef.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;

public class PlayRecipeActivity extends BaseActivity implements SensorEventListener {
    APlayrecipeBinding binding;
    RxRetrofitServices.RecipeService recipeService;
    SensorManager m_clsSensorManager;
    Sensor m_clsSensor;
    SpeechRecognizer mRecognizer;
    int recipeID;
    RecipeViewPagerAdapter recipeViewPagerAdapter;
    RecipeDetail recipeDetailInfo;


    ArrayList<RecipePhase> recipePhases;
    MediaPlayer mediaPlayer;

    int timer_value;
    boolean timerSet = false;
    int sound_count = 0;

    /*
        TODO
        센서근접 -> 음성인식 -> 인식결과에 따라 행동
        시작/다음/이전/타이머 시작/타이머 정지
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = APlayrecipeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recipeService = RxRetrofitBuilder.create(this, RxRetrofitServices.RecipeService.class);

        recipeID = getIntent().getIntExtra("recipeID", 0);

        final String[] permissions = {
                Manifest.permission.RECORD_AUDIO
        };

        if (!Utils.checkPermission(this, permissions))
            ActivityCompat.requestPermissions(this, permissions, 1);

        m_clsSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        m_clsSensor = m_clsSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        binding.playrecipeCircularTimer.setOnClickListener(this::onClickTimer);
        binding.playrecipeCircularTimerText.setOnClickListener(this::onClickTimer);

        getData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        m_clsSensorManager.registerListener(this, m_clsSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    void getData() {
        if (App.isServerAlive()) {
            setPages();
        } else {
            recipeDetailInfo = DataGenerator.make(getResources(), getResources().getInteger(R.integer.DATA_TYPE_RECIPE_DETAIL));
            recipePhases = recipeDetailInfo.getPhases();

            setPages();
        }
    }

    void setPages() {
        recipeViewPagerAdapter = new RecipeViewPagerAdapter(getSupportFragmentManager());

        recipeViewPagerAdapter.addFragment(new PlayRecipeStartFragment(), recipeDetailInfo);
        for (int i = 0; i < recipePhases.size(); i++) {
            if (i == recipePhases.size() - 1)
                recipeViewPagerAdapter.addFragment(new PlayRecipeEndFragment(), recipePhases.get(i));
            else
                recipeViewPagerAdapter.addFragment(new PlayRecipeItemFragment(), recipePhases.get(i));
        }
        binding.playrecipeViewpager.setAdapter(recipeViewPagerAdapter);
        binding.playrecipeViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                binding.playrecipeCircularToolsGroup.setVisibility(
                        position == 0  || position == recipePhases.size()? View.GONE : View.VISIBLE);

                if (timerSet)
                    stopTimer();

                if(mediaPlayer !=null && mediaPlayer.isPlaying())
                    mediaPlayer.stop();

                setTimer(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        binding.playrecipePodslider.setupWithViewPager(binding.playrecipeViewpager);
        binding.playrecipePodslider.setClickable(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        m_clsSensorManager.unregisterListener(this);
        mediaPlayer.release();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float dbDistance = event.values[0];

        if (dbDistance <= 2)
            startListening();

    }

    void onClickTimer(View v) {
        if (!timerSet)
            startTimer();
        else
            stopTimer();
    }

    void setTimer(int position) {
        String restr = recipePhases.get(position-1).getTime_amount().replaceAll("[^0-9]","");
        timer_value = Integer.parseInt(restr) * 60;
        binding.playrecipeCircularTimer.setMax(timer_value);
        binding.playrecipeCircularTimer.setProgressCompat(0, true);
        binding.playrecipeCircularTimerText.setText(String.format("%02d:%02d", timer_value / 60, timer_value % 60));
    }

    void startTimer() {
        timerSet = true;
        binding.playrecipeCircularTimer.setMax(timer_value);
        binding.playrecipeCircularTimerText.setText(String.format("%02d:%02d", timer_value / 60, timer_value % 60));
        compositeDisposable.add(
                Observable.interval(0, 1, TimeUnit.SECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .takeUntil(aLong -> aLong == timer_value)
                        .subscribe(aLong -> {
                            long t = timer_value - aLong;
                            binding.playrecipeCircularTimer.setProgressCompat((int) t, true);
                            binding.playrecipeCircularTimerText.setText(String.format("%02d:%02d", t / 60, t % 60));
                        }, throwable -> throwable.printStackTrace(), () -> {
                            mediaPlayer = MediaPlayer.create(PlayRecipeActivity.this, R.raw.ring_complete);
                            mediaPlayer.setOnCompletionListener(mp -> {
                                if(sound_count < 2) {
                                    sound_count++;
                                    mediaPlayer.seekTo(0);
                                    mediaPlayer.start();
                                }
                                else{
                                    mediaPlayer.stop();
                                    stopTimer();
                                }
                            });
                            mediaPlayer.start();

                        })
        );
    }

    void stopTimer() {
        timerSet = false;
        sound_count = 0;
        compositeDisposable.clear();
        Utils.log(Long.valueOf(timer_value).intValue(), binding.playrecipeCircularTimer.getMax());
        binding.playrecipeCircularTimer.setMax(timer_value);
        binding.playrecipeCircularTimer.setProgressCompat(0, true);
        binding.playrecipeCircularTimerText.setText(String.format("%02d:%02d", timer_value / 60, timer_value % 60));
    }

    void startListening() {
        mRecognizer = null;
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                Log.w("dd", " rfs");
            }

            @Override
            public void onBeginningOfSpeech() {
                Log.w("dd", " bos");
            }

            @Override
            public void onRmsChanged(float rmsdB) {
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
            }

            @Override
            public void onEndOfSpeech() {
            }

            @Override
            public void onError(int error) {
                App.getAppInstance().showToast("음성 인식 실패");
            }

            @Override
            public void onResults(Bundle results) {
                String key = SpeechRecognizer.RESULTS_RECOGNITION;
                ArrayList<String> mResult = results.getStringArrayList(key);
                String[] rs = new String[mResult.size()];
                mResult.toArray(rs);
                binding.playrecipeTts.setText(rs[0]);

                switch (rs[0]) {
                    case "다음":
                        if (binding.playrecipeViewpager.getCurrentItem() != recipePhases.size())
                            binding.playrecipeViewpager.setCurrentItem(binding.playrecipeViewpager.getCurrentItem() + 1);
                        break;
                    case "이전":
                        if (binding.playrecipeViewpager.getCurrentItem() != 0)
                            binding.playrecipeViewpager.setCurrentItem(binding.playrecipeViewpager.getCurrentItem() - 1);
                        break;
                    case "시작":
                        if (binding.playrecipeViewpager.getCurrentItem() != 0 || binding.playrecipeViewpager.getCurrentItem() != recipePhases.size() - 1)
                            startTimer();
                        break;
                    case "정지":
                        if (binding.playrecipeViewpager.getCurrentItem() != 0 || binding.playrecipeViewpager.getCurrentItem() != recipePhases.size() - 1)
                            stopTimer();
                        break;
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
            }
        });

        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                .putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName())
                .putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
        mRecognizer.startListening(i);
    }

    void stopListening() {
        mRecognizer.stopListening();
    }

}