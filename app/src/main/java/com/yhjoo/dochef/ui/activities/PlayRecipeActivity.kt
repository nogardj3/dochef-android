package com.yhjoo.dochef.activities

import android.Manifest
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.yhjoo.dochef.App
import com.yhjoo.dochef.App.Companion.appInstance
import com.yhjoo.dochef.R
import com.yhjoo.dochef.adapter.RecipeViewPagerAdapter
import com.yhjoo.dochef.data.DataGenerator
import com.yhjoo.dochef.databinding.APlayrecipeBinding
import com.yhjoo.dochef.ui.fragments.PlayRecipeEndFragment
import com.yhjoo.dochef.ui.fragments.PlayRecipeItemFragment
import com.yhjoo.dochef.ui.fragments.PlayRecipeStartFragment
import com.yhjoo.dochef.utils.RxRetrofitServices.RecipeService
import com.yhjoo.dochef.data.model.RecipeDetail
import com.yhjoo.dochef.data.model.RecipePhase
import com.yhjoo.dochef.utils.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import java.util.*
import java.util.concurrent.TimeUnit

class PlayRecipeActivity : BaseActivity(), SensorEventListener {
    var binding: APlayrecipeBinding? = null
    var recipeService: RecipeService? = null
    var m_clsSensorManager: SensorManager? = null
    var m_clsSensor: Sensor? = null
    var mRecognizer: SpeechRecognizer? = null
    var recipeViewPagerAdapter: RecipeViewPagerAdapter? = null
    var recipeDetailInfo: RecipeDetail? = null
    var recipePhases: ArrayList<RecipePhase?>? = null
    var mediaPlayer: MediaPlayer? = null
    var timer_value = 0
    var timerSet = false
    var sound_count = 0

    /*
        TODO
        센서근접 -> 음성인식 -> 인식결과에 따라 행동
        시작/다음/이전/타이머 시작/타이머 정지
    */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = APlayrecipeBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        recipeService = RxRetrofitBuilder.create(this, RecipeService::class.java)
        recipeDetailInfo = intent.getSerializableExtra("recipe") as RecipeDetail?
        recipePhases = recipeDetailInfo.getPhases()
        val permissions = arrayOf<String?>(
            Manifest.permission.RECORD_AUDIO
        )
        if (!Utils.checkPermission(this, permissions)) ActivityCompat.requestPermissions(
            this,
            permissions,
            1
        )
        m_clsSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        m_clsSensor = m_clsSensorManager!!.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        binding!!.playrecipeCircularTimer.setOnClickListener { v: View? -> onClickTimer(v) }
        binding!!.playrecipeCircularTimerText.setOnClickListener { v: View? -> onClickTimer(v) }
        data
    }

    override fun onResume() {
        super.onResume()
        m_clsSensorManager!!.registerListener(this, m_clsSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    val data: Unit
        get() {
            if (App.isServerAlive()) {
                setPages()
            } else {
                recipeDetailInfo = DataGenerator.make(
                    resources,
                    resources.getInteger(R.integer.DATA_TYPE_RECIPE_DETAIL)
                )
                recipePhases = recipeDetailInfo.getPhases()
                setPages()
            }
        }

    fun setPages() {
        recipeViewPagerAdapter = RecipeViewPagerAdapter(supportFragmentManager)
        recipeViewPagerAdapter!!.addFragment(PlayRecipeStartFragment(), recipeDetailInfo)
        for (i in recipePhases!!.indices) {
            if (i == recipePhases!!.size - 1) recipeViewPagerAdapter!!.addFragment(
                PlayRecipeEndFragment(),
                recipePhases!![i],
                recipeDetailInfo
            ) else recipeViewPagerAdapter!!.addFragment(PlayRecipeItemFragment(), recipePhases!![i])
        }
        binding!!.playrecipeViewpager.adapter = recipeViewPagerAdapter
        binding!!.playrecipeViewpager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                binding!!.playrecipeCircularToolsGroup.visibility =
                    if (position == 0 || position == recipePhases!!.size) View.GONE else View.VISIBLE
                if (timerSet) stopTimer()
                if (mediaPlayer != null && mediaPlayer!!.isPlaying) mediaPlayer!!.stop()
                if (position != 0) setTimer(position)
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        binding!!.playrecipePodslider.setupWithViewPager(binding!!.playrecipeViewpager)
        binding!!.playrecipePodslider.isClickable = false
    }

    override fun onDestroy() {
        super.onDestroy()
        m_clsSensorManager!!.unregisterListener(this)
        if (mediaPlayer != null) mediaPlayer!!.release()
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    override fun onSensorChanged(event: SensorEvent) {
        val dbDistance = event.values[0]
        if (dbDistance <= 2) startListening()
    }

    fun onClickTimer(v: View?) {
        if (!timerSet) startTimer() else stopTimer()
    }

    fun setTimer(position: Int) {
        val restr = recipePhases!![position - 1].getTime_amount().replace("[^0-9]".toRegex(), "")
        timer_value = restr.toInt() * 60
        binding!!.playrecipeCircularTimer.max = timer_value
        binding!!.playrecipeCircularTimer.setProgressCompat(0, true)
        binding!!.playrecipeCircularTimerText.text =
            String.format("%02d:%02d", timer_value / 60, timer_value % 60)
    }

    fun startTimer() {
        timerSet = true
        binding!!.playrecipeCircularTimer.max = timer_value
        binding!!.playrecipeCircularTimerText.text =
            String.format("%02d:%02d", timer_value / 60, timer_value % 60)
        compositeDisposable!!.add(
            Observable.interval(0, 1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .takeUntil { aLong: Long -> aLong == timer_value.toLong() }
                .subscribe({ aLong: Long ->
                    val t = timer_value - aLong
                    binding!!.playrecipeCircularTimer.setProgressCompat(t.toInt(), true)
                    binding!!.playrecipeCircularTimerText.text =
                        String.format("%02d:%02d", t / 60, t % 60)
                }, { throwable: Throwable -> throwable.printStackTrace() }) {
                    mediaPlayer = MediaPlayer.create(this@PlayRecipeActivity, R.raw.ring_complete)
                    mediaPlayer.setOnCompletionListener(OnCompletionListener { mp: MediaPlayer? ->
                        if (sound_count < 2) {
                            sound_count++
                            mediaPlayer.seekTo(0)
                            mediaPlayer.start()
                        } else {
                            mediaPlayer.stop()
                            stopTimer()
                        }
                    })
                    mediaPlayer.start()
                }
        )
    }

    fun stopTimer() {
        timerSet = false
        sound_count = 0
        compositeDisposable!!.clear()
        Utils.log(
            java.lang.Long.valueOf(timer_value.toLong()).toInt(),
            binding!!.playrecipeCircularTimer.max
        )
        binding!!.playrecipeCircularTimer.max = timer_value
        binding!!.playrecipeCircularTimer.setProgressCompat(0, true)
        binding!!.playrecipeCircularTimerText.text =
            String.format("%02d:%02d", timer_value / 60, timer_value % 60)
    }

    fun startListening() {
        mRecognizer = null
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        mRecognizer.setRecognitionListener(object : RecognitionListener {
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
                appInstance!!.showToast("음성 인식 실패")
            }

            override fun onResults(results: Bundle) {
                val key = SpeechRecognizer.RESULTS_RECOGNITION
                val mResult = results.getStringArrayList(key)
                val rs = arrayOfNulls<String>(mResult!!.size)
                mResult.toArray(rs)
                binding!!.playrecipeTts.text = rs[0]
                when (rs[0]) {
                    "다음" -> if (binding!!.playrecipeViewpager.currentItem != recipePhases!!.size) binding!!.playrecipeViewpager.currentItem =
                        binding!!.playrecipeViewpager.currentItem + 1
                    "이전" -> if (binding!!.playrecipeViewpager.currentItem != 0) binding!!.playrecipeViewpager.currentItem =
                        binding!!.playrecipeViewpager.currentItem - 1
                    "시작" -> if (binding!!.playrecipeViewpager.currentItem != 0 || binding!!.playrecipeViewpager.currentItem != recipePhases!!.size - 1) startTimer()
                    "정지" -> if (binding!!.playrecipeViewpager.currentItem != 0 || binding!!.playrecipeViewpager.currentItem != recipePhases!!.size - 1) stopTimer()
                }
            }

            override fun onPartialResults(partialResults: Bundle) {}
            override fun onEvent(eventType: Int, params: Bundle) {}
        })
        val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            .putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
            .putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
        mRecognizer.startListening(i)
    }

    fun stopListening() {
        mRecognizer!!.stopListening()
    }
}