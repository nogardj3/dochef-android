package com.yhjoo.dochef.ui.activities

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
import androidx.core.app.ActivityCompat
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.adapter.PlayRecipeViewPagerAdapter
import com.yhjoo.dochef.databinding.APlayrecipeBinding
import com.yhjoo.dochef.db.DataGenerator
import com.yhjoo.dochef.model.RecipeDetail
import com.yhjoo.dochef.model.RecipePhase
import com.yhjoo.dochef.ui.fragments.*
import com.yhjoo.dochef.utilities.*
import com.yhjoo.dochef.utilities.RetrofitServices.RecipeService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import java.util.*
import java.util.concurrent.TimeUnit

class PlayRecipeActivity : BaseActivity(), SensorEventListener {
    /*
        TODO
        센서근접 -> 음성인식 -> 인식결과에 따라 행동
        시작/다음/이전/타이머 시작/타이머 정지
    */

    private val binding: APlayrecipeBinding by lazy { APlayrecipeBinding.inflate(layoutInflater) }

    private lateinit var recipeService: RecipeService
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var playRecipeViewPagerAdapter: PlayRecipeViewPagerAdapter
    private lateinit var recipeDetailInfo: RecipeDetail
    private lateinit var recipePhases: ArrayList<RecipePhase>

    private lateinit var sensorManager: SensorManager
    private var mClssensor: Sensor? = null
    private var mediaPlayer: MediaPlayer? = null

    private var timerValue = 0
    private var timerSet = false
    private var soundCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        recipeService = RetrofitBuilder.create(this, RecipeService::class.java)
        recipeDetailInfo = intent.getSerializableExtra("recipe") as RecipeDetail
        recipePhases = recipeDetailInfo.phases

        val permissions = arrayOf(Manifest.permission.RECORD_AUDIO)
        if (!Utils.checkPermission(this, permissions)) ActivityCompat.requestPermissions(
            this,
            permissions,
            1
        )

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        mClssensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        binding.playrecipeCircularTimer.setOnClickListener { onClickTimer() }
        binding.playrecipeCircularTimerText.setOnClickListener { onClickTimer() }

        if (App.isServerAlive) {
            setPages()
        } else {
            recipeDetailInfo = DataGenerator.make(
                resources,
                resources.getInteger(R.integer.DATA_TYPE_RECIPE_DETAIL)
            )
            recipePhases = recipeDetailInfo.phases
            setPages()
        }
    }

    override fun onResume() {
        super.onResume()

        sensorManager.registerListener(this, mClssensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    private fun setPages() {
        playRecipeViewPagerAdapter = PlayRecipeViewPagerAdapter(supportFragmentManager)
        playRecipeViewPagerAdapter.addFragment(PlayRecipeStartFragment(), recipeDetailInfo)
        for (i in recipePhases.indices) {
            if (i == recipePhases.size - 1) playRecipeViewPagerAdapter.addFragment(
                PlayRecipeEndFragment(),
                recipePhases[i],
                recipeDetailInfo
            ) else playRecipeViewPagerAdapter.addFragment(PlayRecipeItemFragment(), recipePhases[i])
        }
        binding.playrecipeViewpager.adapter = playRecipeViewPagerAdapter
        binding.playrecipeViewpager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                binding.playrecipeCircularToolsGroup.visibility =
                    if (position == 0 || position == recipePhases.size) View.GONE else View.VISIBLE
                if (timerSet) stopTimer()
                if (mediaPlayer != null && mediaPlayer!!.isPlaying) mediaPlayer!!.stop()
                if (position != 0) setTimer(position)
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        binding.playrecipeStepindicator.setupWithViewPager(binding.playrecipeViewpager)
        binding.playrecipeStepindicator.isClickable = false
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

    private fun onClickTimer() {
        if (!timerSet) startTimer() else stopTimer()
    }

    private fun setTimer(position: Int) {
        val restr = recipePhases[position - 1].time_amount.replace("[^0-9]".toRegex(), "")
        timerValue = restr.toInt() * 60
        binding.playrecipeCircularTimer.max = timerValue
        binding.playrecipeCircularTimer.setProgressCompat(0, true)
        binding.playrecipeCircularTimerText.text =
            String.format("%02d:%02d", timerValue / 60, timerValue % 60)
    }

    private fun startTimer() {
        timerSet = true
        binding.playrecipeCircularTimer.max = timerValue
        binding.playrecipeCircularTimerText.text =
            String.format("%02d:%02d", timerValue / 60, timerValue % 60)
        compositeDisposable.add(
            Observable.interval(0, 1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .takeUntil { aLong: Long -> aLong == timerValue.toLong() }
                .subscribe({ aLong: Long ->
                    val t = timerValue - aLong
                    binding.playrecipeCircularTimer.setProgressCompat(t.toInt(), true)
                    binding.playrecipeCircularTimerText.text =
                        String.format("%02d:%02d", t / 60, t % 60)
                }, { throwable: Throwable -> throwable.printStackTrace() }) {
                    mediaPlayer = MediaPlayer.create(this@PlayRecipeActivity, R.raw.ring_complete)
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

    private fun stopTimer() {
        timerSet = false
        soundCount = 0
        compositeDisposable.clear()

        binding.playrecipeCircularTimer.max = timerValue
        binding.playrecipeCircularTimer.setProgressCompat(0, true)
        binding.playrecipeCircularTimerText.text =
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
            }

            override fun onResults(results: Bundle) {
                val key = SpeechRecognizer.RESULTS_RECOGNITION
                val mResult = results.getStringArrayList(key)!!
                val rs = arrayOfNulls<String>(mResult.size)

                mResult.toArray(rs)
                binding.playrecipeTts.text = rs[0]
                when (rs[0]) {
                    "다음" -> if (binding.playrecipeViewpager.currentItem != recipePhases.size) binding.playrecipeViewpager.currentItem =
                        binding.playrecipeViewpager.currentItem + 1
                    "이전" -> if (binding.playrecipeViewpager.currentItem != 0) binding.playrecipeViewpager.currentItem =
                        binding.playrecipeViewpager.currentItem - 1
                    "시작" -> if (binding.playrecipeViewpager.currentItem != 0 || binding.playrecipeViewpager.currentItem != recipePhases.size - 1) startTimer()
                    "정지" -> if (binding.playrecipeViewpager.currentItem != 0 || binding.playrecipeViewpager.currentItem != recipePhases.size - 1) stopTimer()
                }
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


/*  Ver2. ViewPager2
    override fun onResume() {
        super.onResume()

        sensorManager.registerListener(this, mClssensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    private fun setPages() {
        recipeViewPagerAdapter = RecipeViewPagerAdapter(supportFragmentManager)
        recipeViewPagerAdapter.addFragment(PlayRecipeStartFragment(), recipeDetailInfo)
        for (i in recipePhases.indices) {
            if (i == recipePhases.size - 1) recipeViewPagerAdapter.addFragment(
                PlayRecipeEndFragment(),
                recipePhases[i],
                recipeDetailInfo
            ) else recipeViewPagerAdapter.addFragment(PlayRecipeItemFragment(), recipePhases[i])
        }
        binding.playrecipeViewpager.adapter = recipeViewPagerAdapter
        binding.playrecipeViewpager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                binding.playrecipeCircularToolsGroup.visibility =
                    if (position == 0 || position == recipePhases.size) View.GONE else View.VISIBLE
                if (timerSet) stopTimer()
                if (mediaPlayer!=null && mediaPlayer!!.isPlaying) mediaPlayer!!.stop()
                if (position != 0) setTimer(position)
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        binding.playrecipePodslider.setupWithViewPager(binding.playrecipeViewpager)
        binding.playrecipePodslider.isClickable = false
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
        if(mediaPlayer!=null)
            mediaPlayer!!.release()
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    override fun onSensorChanged(event: SensorEvent) {
        val dbDistance = event.values[0]
        if (dbDistance <= 2) startListening()
    }

    private fun onClickTimer() {
        if (!timerSet) startTimer() else stopTimer()
    }

    private fun setTimer(position: Int) {
        val restr = recipePhases[position - 1].time_amount.replace("[^0-9]".toRegex(), "")
        timerValue = restr.toInt() * 60
        binding.playrecipeCircularTimer.max = timerValue
        binding.playrecipeCircularTimer.setProgressCompat(0, true)
        binding.playrecipeCircularTimerText.text =
            String.format("%02d:%02d", timerValue / 60, timerValue % 60)
    }

    private fun startTimer() {
        timerSet = true
        binding.playrecipeCircularTimer.max = timerValue
        binding.playrecipeCircularTimerText.text =
            String.format("%02d:%02d", timerValue / 60, timerValue % 60)
        compositeDisposable.add(
            Observable.interval(0, 1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .takeUntil { aLong: Long -> aLong == timerValue.toLong() }
                .subscribe({ aLong: Long ->
                    val t = timerValue - aLong
                    binding.playrecipeCircularTimer.setProgressCompat(t.toInt(), true)
                    binding.playrecipeCircularTimerText.text =
                        String.format("%02d:%02d", t / 60, t % 60)
                }, { throwable: Throwable -> throwable.printStackTrace() }) {
                    mediaPlayer = MediaPlayer.create(this@PlayRecipeActivity, R.raw.ring_complete)
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

    private fun stopTimer() {
        timerSet = false
        soundCount = 0
        compositeDisposable.clear()

        binding.playrecipeCircularTimer.max = timerValue
        binding.playrecipeCircularTimer.setProgressCompat(0, true)
        binding.playrecipeCircularTimerText.text =
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
            }

            override fun onResults(results: Bundle) {
                val key = SpeechRecognizer.RESULTS_RECOGNITION
                val mResult = results.getStringArrayList(key)!!
                val rs = arrayOfNulls<String>(mResult.size)

                mResult.toArray(rs)
                binding.playrecipeTts.text = rs[0]
                when (rs[0]) {
                    "다음" -> if (binding.playrecipeViewpager.currentItem != recipePhases.size) binding.playrecipeViewpager.currentItem =
                        binding.playrecipeViewpager.currentItem + 1
                    "이전" -> if (binding.playrecipeViewpager.currentItem != 0) binding.playrecipeViewpager.currentItem =
                        binding.playrecipeViewpager.currentItem - 1
                    "시작" -> if (binding.playrecipeViewpager.currentItem != 0 || binding.playrecipeViewpager.currentItem != recipePhases.size - 1) startTimer()
                    "정지" -> if (binding.playrecipeViewpager.currentItem != 0 || binding.playrecipeViewpager.currentItem != recipePhases.size - 1) stopTimer()
                }
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

    class RecipeViewPagerAdapter(fragmentActivity: FragmentActivity,size: Int, detail :RecipeDetail, phase: RecipePhase)
        : FragmentStateAdapter(fragmentActivity) {

        override fun getItemCount(): Int {
            return size
        }

        override fun createFragment(position: Int): Fragment {
            return when (position){
                0-> {
                    val fragment =  PlayRecipeStartFragment()
                    fragment.arguments = bundleOf(
                        "item" to detail
                    )
                    fragment
                }
                fragments.size -1 -> MainRecipesFragment()
                else -> PlayRecipeItemFragment()
            }
        }
    }
*/
}