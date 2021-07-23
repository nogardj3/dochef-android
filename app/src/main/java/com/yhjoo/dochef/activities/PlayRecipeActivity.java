package com.yhjoo.dochef.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import com.bhargavms.podslider.PodSlider;
import com.github.florent37.viewanimator.ViewAnimator;
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.adapter.RecipeViewPagerAdapter;
import com.yhjoo.dochef.databinding.APlayrecipeBinding;
import com.yhjoo.dochef.fragments.PlayRecipeEndFragment;
import com.yhjoo.dochef.fragments.PlayRecipeItemFragment;
import com.yhjoo.dochef.fragments.PlayRecipeStartFragment;
import com.yhjoo.dochef.model.RecipePlay;
import com.yhjoo.dochef.utils.Utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;

public class PlayRecipeActivity extends BaseActivity implements SensorEventListener {
    APlayrecipeBinding binding;
    SensorManager m_clsSensorManager;
    Sensor m_clsSensor;
    MediaPlayer player;
    SpeechRecognizer mRecognizer;

    ArrayList<RecipePlay> recipePlays;
    CountDownTimer countDownTimer;

    boolean timerSet = false;

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


        String[] ingredients = {"김치1", "김치2"};
        String[] tags = {"태그1", "태그2", "태그3", "태그4", "태그5"};
//        binding.playrecipeTimerFab.setImageResource(R.drawable.ic_alarm_white);

        recipePlays = new ArrayList<>(Arrays.asList(
                new RecipePlay(
                        R.raw.tempimg_playrecipestart,
                        "치즈김치볶음밥!! / 백종원 김치볶음밥, 묵은지해결",
                        ingredients,
                        "김치는\n미리\n썰어요\n",
                        0, null),
                new RecipePlay(
                        R.raw.tempimg_playrecipe1,
                        null,
                        ingredients,
                        "햄과\n 대파1줄을\n 총총\n 썰어주세요",
                        10, null),
                new RecipePlay(
                        R.raw.tempimg_playrecipe2,
                        null,
                        ingredients,
                        "팬에 기름을 넉넉히 두르신후 파를 넣고 볶아서 파기름을 내주세요!",
                        10, null),
                new RecipePlay(
                        R.raw.tempimg_playrecipe3,
                        null,
                        ingredients,
                        "파기름을 냈으면 김치를 넣고 볶아주신뒤, 고추장1큰술을 넣고 잘 섞어주세요!",
                        10, null),
                new RecipePlay(
                        R.raw.tempimg_playrecipe4,
                        null,
                        ingredients,
                        "햄과 밥을 넣고 잘 섞어주신뒤, 참기름 1큰술을 넣고 볶아주세요!",
                        10, null),
                new RecipePlay(
                        R.raw.tempimg_playrecipe4,
                        null,
                        ingredients,
                        "햄과 밥을 넣고 잘 섞어주신뒤, 참기름 1큰술을 넣고 볶아주세요!",
                        10, null),
                new RecipePlay(
                        R.raw.tempimg_playrecipe4,
                        null,
                        ingredients,
                        "햄과 밥을 넣고 잘 섞어주신뒤, 참기름 1큰술을 넣고 볶아주세요!",
                        10, null),
                new RecipePlay(
                        R.raw.tempimg_playrecipe4,
                        null,
                        ingredients,
                        "햄과 밥을 넣고 잘 섞어주신뒤, 참기름 1큰술을 넣고 볶아주세요!",
                        10, null),
                new RecipePlay(
                        R.raw.tempimg_playrecipe4,
                        null,
                        ingredients,
                        "햄과 밥을 넣고 잘 섞어주신뒤, 참기름 1큰술을 넣고 볶아주세요!",
                        10, null),
                new RecipePlay(
                        R.raw.tempimg_playrecipe4,
                        null,
                        ingredients,
                        "햄과 밥을 넣고 잘 섞어주신뒤, 참기름 1큰술을 넣고 볶아주세요!",
                        10, null),

                new RecipePlay(
                        R.raw.tempimg_playrecipefinish,
                        null,
                        ingredients,
                        "밥을 팬 배닥에 평평하게 깔아주시고 위에 모짜렐라치즈를 뿌려주시면 완성!!",
                        0, tags)));


        RecipeViewPagerAdapter recipeViewPagerAdapter = new RecipeViewPagerAdapter(getSupportFragmentManager());

        for (int i = 0; i < recipePlays.size(); i++) {
            if (i == 0)
                recipeViewPagerAdapter.addFragment(new PlayRecipeStartFragment(), recipePlays.get(i));
            else if (i == recipePlays.size() - 1)
                recipeViewPagerAdapter.addFragment(new PlayRecipeEndFragment(), recipePlays.get(i));
            else
                recipeViewPagerAdapter.addFragment(new PlayRecipeItemFragment(), recipePlays.get(i));
        }


        final String[] permissions = {
                Manifest.permission.RECORD_AUDIO
        };

        if (!Utils.checkPermission(this, permissions))
            ActivityCompat.requestPermissions(this, permissions, 1);

        m_clsSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        m_clsSensor = m_clsSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        binding.playrecipeViewpager.setAdapter(recipeViewPagerAdapter);
        binding.playrecipeViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                binding.playrecipeTimerGroup.setVisibility(position == 0
                        || position == recipePlays.size() - 1 ? View.GONE : View.VISIBLE);

                if (timerSet) {
                    timerSet = false;
//                    binding.playrecipeTimerFab.setImageResource(R.drawable.ic_alarm_white);
//                    binding.playrecipeTimerFab.setColorNormal(getResources().getColor(R.color.colorPrimary, null));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        PodSlider podSlider = binding.playrecipePodslider;
        podSlider.setNumberOfPods(recipePlays.size());
        podSlider.setUpWithViewPager(binding.playrecipeViewpager);
        ArrayList<String> arrayList = new ArrayList<>();
        for (int i = 0; i < recipePlays.size(); i++) {
            if (i == 0)
                arrayList.add("S");
            else if (i == recipePlays.size() - 1)
                arrayList.add("E");
            else
                arrayList.add(String.valueOf(i));
        }

        podSlider.setPodTexts(arrayList.toArray(new String[0]));

        binding.playrecipeTimerFab.setOnClickListener(this::toggleFab);
    }

    @Override
    protected void onResume() {
        super.onResume();
        m_clsSensorManager.registerListener(this, m_clsSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        m_clsSensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float dbDistance = event.values[0];

        if (dbDistance <= 2) {
        }
    }

    void startTimer() {
        timerSet = true;
        ViewAnimator.animate(binding.playrecipeTimerFab)
                .onStart(() -> binding.playrecipeTimerFab.setClickable(false))
                .wobble()
                .duration(500)
                .onStop(() -> {
                    binding.playrecipeTimerFab.setClickable(true);
//                    binding.playrecipeTimerFab.setImageResource(R.drawable.ic_alarm_white_off);
//                    binding.playrecipeTimerFab.setColorNormal(getResources().getColor(R.color.colorSecondary, null));
                })
                .start();
        countDownTimer = new CountDownTimer(5000, 1000) {
            public void onTick(long millisUntilFinished) {

                NumberFormat f = new DecimalFormat("00");
                long min = (millisUntilFinished / 60000) % 60;
                long sec = (millisUntilFinished / 1000) % 60;

                binding.playrecipeTimerText.setText(f.format(min) + ":" + f.format(sec));
            }

            public void onFinish() {
                binding.playrecipeTimerText.setText("00:00");
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                player = MediaPlayer.create(PlayRecipeActivity.this, notification);
                player.setLooping(true);
                player.start();
            }
        }.start();
    }

    void toggleFab(View v) {
        if (timerSet) {
            stopTimer();
        } else {
            startTimer();
        }
    }

    void stopTimer() {
        timerSet = false;
        ViewAnimator.animate(binding.playrecipeTimerFab)
                .onStart(() -> binding.playrecipeTimerFab.setClickable(false))
                .tada()
                .duration(500)
                .onStop(() -> {
                    binding.playrecipeTimerFab.setClickable(true);
//                    binding.playrecipeTimerFab.setImageResource(R.drawable.ic_alarm_white);
//                    binding.playrecipeTimerFab.setColorNormal(getResources().getColor(R.color.colorPrimary, null));
                })
                .start();

        binding.playrecipeTimerText.setText("00:00");
        countDownTimer.cancel();
        if (player != null && player.isPlaying())
            player.stop();
    }

    void startListening() {
        Observable.timer(10, TimeUnit.MILLISECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(count -> {
                    Log.w("dd", "hello2");
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
                            Log.w("dd", rs[0]);
                            binding.playrecipeTts.setText(rs[0]);

                            switch (rs[0]) {
                                case "다음":
                                    if (binding.playrecipeViewpager.getCurrentItem() != recipePlays.size() - 1)
                                        binding.playrecipeViewpager.setCurrentItem(binding.playrecipeViewpager.getCurrentItem() + 1);
                                    break;
                                case "이전":
                                    if (binding.playrecipeViewpager.getCurrentItem() != 0)
                                        binding.playrecipeViewpager.setCurrentItem(binding.playrecipeViewpager.getCurrentItem() - 1);
                                    break;
                                case "재료":
                                    if (binding.playrecipeViewpager.getCurrentItem() != 0 || binding.playrecipeViewpager.getCurrentItem() != recipePlays.size() - 1) {
                                        String temp = "";
                                        for (int i = 0; i < recipePlays.get(binding.playrecipeViewpager.getCurrentItem()).getIngredients().length; i++)
                                            temp += (recipePlays.get(binding.playrecipeViewpager.getCurrentItem()).getIngredients()[i] + " ");
                                    }
                                    break;
                                case "시작":
                                    if (binding.playrecipeViewpager.getCurrentItem() != 0 || binding.playrecipeViewpager.getCurrentItem() != recipePlays.size() - 1)
                                        startTimer();
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

                    Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
                    i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
                    mRecognizer.startListening(i);
                });
    }
}