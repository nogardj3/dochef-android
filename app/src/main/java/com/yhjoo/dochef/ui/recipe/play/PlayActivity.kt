package com.yhjoo.dochef.ui.recipe.play

import android.Manifest
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.*
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.model.RecipePhase
import com.yhjoo.dochef.databinding.RecipeplayActivityBinding
import com.yhjoo.dochef.ui.base.BaseActivity
import com.yhjoo.dochef.utils.*
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import java.util.*
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class PlayActivity : BaseActivity(), SensorEventListener {
    private val binding: RecipeplayActivityBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.recipeplay_activity)
    }
    private val recipeplayViewModel: RecipePlayViewModel by viewModels()
    private lateinit var recipePlayFragmentAdapter: RecipePlayFragmentAdapter

    private lateinit var speechRecognizer: SpeechRecognizer

    private lateinit var sensorManager: SensorManager
    private var mClssensor: Sensor? = null
    private var mediaPlayer: MediaPlayer? = null

    private var timerValue = 0
    private var timerSet = false
    private var soundCount = 0

    private var recipePhases = ArrayList<RecipePhase>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val permissions = arrayOf(Manifest.permission.RECORD_AUDIO)
        if (!OtherUtil.checkPermission(this, permissions)) ActivityCompat.requestPermissions(
            this,
            permissions,
            1
        )

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        mClssensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

        binding.apply {
            lifecycleOwner = this@PlayActivity
            activity = this@PlayActivity

            recipePhases = recipeplayViewModel.recipePhase

            recipePlayFragmentAdapter = RecipePlayFragmentAdapter(this@PlayActivity)
            recipeplayViewpager.also {
                it.offscreenPageLimit = 5
                it.adapter = recipePlayFragmentAdapter
                it.setPageTransformer(MarginPageTransformer(15))
                it.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageScrolled(
                        position: Int,
                        positionOffset: Float,
                        positionOffsetPixels: Int
                    ) {
                    }

                    override fun onPageSelected(position: Int) {
                        binding.recipeplayCircularToolsGroup.isGone =
                            position == 0 || position == recipePhases.size
                        if (timerSet) stopTimer()
                        if (mediaPlayer != null && mediaPlayer!!.isPlaying) mediaPlayer!!.stop()
                        if (position != 0) setTimer(position)
                    }

                    override fun onPageScrollStateChanged(state: Int) {}
                })
            }
            recipeplayPageindicator.setViewPager2(recipeplayViewpager)
        }
    }

    override fun onResume() {
        super.onResume()

        sensorManager.registerListener(this, mClssensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
        if (mediaPlayer != null)
            mediaPlayer!!.release()
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent) {
        val dbDistance = event.values[0]
        if (dbDistance <= 2) startListening()
    }

    fun onClickTimer() {
        if (!timerSet) startTimer() else stopTimer()
    }

    fun startTimer() {
        timerSet = true
        binding.recipeplayCircularTimer.max = timerValue
        binding.recipeplayCircularTimerText.text =
            String.format("%02d:%02d", timerValue / 60, timerValue % 60)
        compositeDisposable.add(
            Observable.interval(0, 1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .takeUntil { aLong: Long -> aLong == timerValue.toLong() }
                .subscribe({ aLong: Long ->
                    val t = timerValue - aLong
                    binding.recipeplayCircularTimer.setProgressCompat(t.toInt(), true)
                    binding.recipeplayCircularTimerText.text =
                        String.format("%02d:%02d", t / 60, t % 60)
                }, { throwable: Throwable -> throwable.printStackTrace() }) {
                    mediaPlayer = MediaPlayer.create(this@PlayActivity, R.raw.ring_complete)
                    mediaPlayer!!.setOnCompletionListener {
                        if (soundCount < 2) {
                            soundCount++
                            it.seekTo(0)
                            it.start()
                        } else {
                            mediaPlayer!!.stop()
                            stopTimer()
                        }
                    }
                    mediaPlayer!!.start()
                }
        )
    }

    fun setTimer(position: Int) {
        val restr = recipePhases[position - 1].time_amount.replace("[^0-9]".toRegex(), "")
        timerValue = restr.toInt() * 60
        binding.recipeplayCircularTimer.max = timerValue
        binding.recipeplayCircularTimer.setProgressCompat(0, true)
        binding.recipeplayCircularTimerText.text =
            String.format("%02d:%02d", timerValue / 60, timerValue % 60)
    }

    fun stopTimer() {
        timerSet = false
        soundCount = 0
        compositeDisposable.clear()

        binding.recipeplayCircularTimer.max = timerValue
        binding.recipeplayCircularTimer.setProgressCompat(0, true)
        binding.recipeplayCircularTimerText.text =
            String.format("%02d:%02d", timerValue / 60, timerValue % 60)
    }

    private fun startListening() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle) {
                Log.w("dd", " rfs")
            }

            override fun onBeginningOfSpeech() {
                Log.w("dd", " bos")
            }

            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray) {}
            override fun onEndOfSpeech() {}
            override fun onError(error: Int) {
                App.showToast("음성 인식 실패")
                stopListening()
            }

            override fun onResults(results: Bundle) {
                val key = SpeechRecognizer.RESULTS_RECOGNITION
                val mResult = results.getStringArrayList(key)!!
                val rs = arrayOfNulls<String>(mResult.size)

                mResult.toArray(rs)
                binding.recipeplayTts.text = rs[0]
                when (rs[0]) {
                    "다음" -> if (binding.recipeplayViewpager.currentItem != recipePhases.size) binding.recipeplayViewpager.currentItem =
                        binding.recipeplayViewpager.currentItem + 1
                    "이전" -> if (binding.recipeplayViewpager.currentItem != 0) binding.recipeplayViewpager.currentItem =
                        binding.recipeplayViewpager.currentItem - 1
                    "시작" -> if (binding.recipeplayViewpager.currentItem != 0 || binding.recipeplayViewpager.currentItem != recipePhases.size - 1) startTimer()
                    "정지" -> if (binding.recipeplayViewpager.currentItem != 0 || binding.recipeplayViewpager.currentItem != recipePhases.size - 1) stopTimer()
                }
                stopListening()
            }

            override fun onPartialResults(partialResults: Bundle) {}
            override fun onEvent(eventType: Int, params: Bundle) {}
        })
        val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            .putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
            .putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")

        speechRecognizer.startListening(i)
    }

    private fun stopListening() {
        speechRecognizer.stopListening()
    }

    inner class RecipePlayFragmentAdapter(fragmentActivity: FragmentActivity) :
        FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int {
            return recipePhases.size + 1
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> PlayStartFragment()
                recipePhases.size -> PlayEndFragment()
                else -> {
                    val fragment = PlayItemFragment()
                    fragment.arguments = bundleOf(
                        Pair("position", position - 1)
                    )
                    fragment
                }
            }
        }
    }
}