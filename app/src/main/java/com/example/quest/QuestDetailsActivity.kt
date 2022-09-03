package com.example.quest

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.quest.adapters.ViewPagerAdapter
import com.example.quest.databinding.ActivityQuestDetailsBinding
import com.example.quest.model.Quest
import com.example.quest.model.QuestsList
import com.example.quest.utils.AlertHelper
import com.example.quest.utils.Prefs
import com.example.quest.utils.putAny
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.ScanOptions
import org.w3c.dom.Text
import java.util.jar.Attributes
import kotlin.concurrent.fixedRateTimer

class QuestDetailsActivity : AppCompatActivity(),SensorEventListener{
    lateinit var binding: ActivityQuestDetailsBinding
    lateinit var seekBar:SeekBar

    lateinit var sensorManager: SensorManager
    var totalSteps = 0f
    var previousSteps = 0f
    var running = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =  ActivityQuestDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent = intent.extras
        val quest = intent?.getSerializable("Quest") as Quest
        setUi(quest)
        loadStepsData()
        sensorManager=getSystemService(SENSOR_SERVICE) as SensorManager
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACTIVITY_RECOGNITION) !=
            PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission
                    .ACTIVITY_RECOGNITION),200)
            }
        }else{
            running =  true
            val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
            if (stepSensor == null){
                Toast.makeText(this, "Device has not step count sensor", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "Device has step count sensor", Toast.LENGTH_SHORT).show()
                sensorManager.registerListener(this,stepSensor,SensorManager.SENSOR_DELAY_UI)
            }
        }

        binding.shareBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_SUBJECT,"Achievement")
            intent.putExtra(Intent.EXTRA_TEXT,"I've completed the task \"${quest.tasks?.get(0)?.name}\" " +
                    "from " +
                    "quest \"${quest.name}\" in QuestApp!!!")
            startActivity(Intent.createChooser(intent,"Share Achievement"))
        }
        quest.tasks?.get(0)?.goalType = "STEPS"
        quest.tasks?.get(0)?.goalValue = 100
        if(quest.tasks?.get(0)?.goalType == "STEPS"){
            binding.sendResultBtn.text = "Step Counter"
        }
        binding.sendResultBtn.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.steps_counter_dialog)
            val progressbar = dialog.findViewById<ProgressBar>(R.id.progressBar)
            progressbar.progress = ((totalSteps/quest.tasks?.get(0)?.goalValue!!)*100).toInt()
            val current = dialog.findViewById<TextView>(R.id.current_tv)
            val goal = dialog.findViewById<TextView>(R.id.goal_tv)
            val closeBtn = dialog.findViewById<Button>(R.id.close_dialog_btn)
            current.text = totalSteps.toString()
            goal.text = quest.tasks?.get(0)?.goalValue.toString()
            dialog.show()
            closeBtn.setOnClickListener {
                dialog.dismiss()
            }
        }
        binding.taskGoal.setOnClickListener {
            val integrator = IntentIntegrator(this)
            integrator.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
            integrator.initiateScan()
        }
    }

    private fun loadStepsData() {
        val steps = Prefs.getFloat("STEPS",0f)
        previousSteps = steps
        totalSteps = steps
        //binding.taskTime.text = previousSteps.toString()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = IntentIntegrator.parseActivityResult(resultCode,data)
        if(result !=null && result.contents!=null){
            AlertHelper.showAlert(this,"Scan result","Correct Answer")
        }
    }
    fun setUi(quest: Quest){
        binding.dateTv.text = quest.startDate
        binding.difficultyTv.text = "Difficulty: ${quest.difficulty}"
        binding.userTv.text = quest.authorName
        binding.categoryTv.text = quest.category?.name
        binding.lbl2Tv.text = quest.tasks?.get(0)?.name ?: "Welcome to Khazan"
        binding.lbl1Tv.text =quest.name
        binding.taskDescTv.text =  quest.description
        binding.questDescTv.text =  quest.description
        binding.taskGoal.text = quest.tasks?.get(0)?.goalType ?: "Scan Qr"
        binding.taskDate.text = quest.tasks?.get(0)?.startDate?: "Scan Qr"
        binding.taskTime.text = quest.tasks?.get(0)?.taskCompletionTime.toString()+" min"

        val photos = listOf("http://wsk2019.mad.hakta.pro/uploads/files/b8cf53b7499fcc8b%20(2).jpg",
        "http://wsk2019.mad.hakta.pro/uploads/files/b8cf53b7499fcc8b%20(1).jpg",
        "http://wsk2019.mad.hakta.pro/uploads/files/Kazan-Expo-Kazan-Ekspo%20(1).jpg")
        val vide = "http://wsk2019.mad.hakta.pro/uploads/files/mad.mp4"
        val audio = "http://wsk2019.mad.hakta.pro/uploads/files/song1.mp3"

        binding.viewpager1.adapter = ViewPagerAdapter(this,photos )
        binding.viewpager2.adapter = ViewPagerAdapter(this,photos )

        binding.dotsIndicator.attachTo(binding.viewpager1)
        binding.dotsIndicator2.attachTo(binding.viewpager2)

        binding.videoItem.mediaName.text ="Mad Worldskills"
        binding.videoItem.mediaName.setOnClickListener {
            val dialog =  Dialog(this)
            dialog.setContentView(R.layout.video_dialog)
            dialog.show()
            val videoView = dialog.findViewById<VideoView>(R.id.video_view)
            val closeBtn = dialog.findViewById<TextView>(R.id.close_btn)
            val playPauseButton = findViewById<ImageView>(R.id.playPauseButton)
            videoView?.setOnCompletionListener {

            }
            val runningTime = dialog.findViewById<TextView>(R.id.runningTime)
            runningTime?.setText("00:00")
            videoView?.setOnPreparedListener {
                seekBar.max = videoView.duration
                val fixedRateTimer = fixedRateTimer(name = "hello-timer",
                    initialDelay = 0, period = 1000) {
                    seekBar?.setProgress(videoView?.getCurrentPosition()!!);

                    if (videoView?.isPlaying()!! == false) {
                    }

                    var time = videoView?.getCurrentPosition()!! / 1000;
                    var minute = time / 60;
                    var second = time % 60;

                    runOnUiThread {
                        runningTime?.setText(minute.toString() + ":" + second.toString());
                    }
                }
            }
            var currentPosition =0
            playPauseButton?.setOnClickListener {
                if(videoView.isPlaying) {
                    currentPosition = videoView.currentPosition
                    videoView.stopPlayback()
                    playPauseButton.setImageResource(R.drawable.play_icon)
                }else{
                    videoView.resume()
                    videoView.seekTo(currentPosition)
                    playPauseButton.setImageResource(R.drawable.ic_baseline_pause_24)
                }

            }

            val stopButton = dialog.findViewById<ImageView>(R.id.stopButton)
            stopButton?.setOnClickListener {

            }

            seekBar = dialog.findViewById<SeekBar>(R.id.seekBar)
            seekBar?.setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                }

            })
            closeBtn.setOnClickListener {
                videoView.stopPlayback()
                dialog.dismiss()
            }
            val uri = Uri.parse(vide)
            videoView.setVideoURI(uri)
            videoView.start()
        }

        binding.audioItem.mediaName.text ="Song"
        binding.audioItem.mediaName.setOnClickListener {

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED && requestCode == 200){

        }else{
            ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACTIVITY_RECOGNITION)
        }
    }
    override fun onSensorChanged(event: SensorEvent?) {
        Toast.makeText(this, "changed", Toast.LENGTH_SHORT).show()
            totalSteps = event!!.values[0]
            Prefs.putAny("STEPS",totalSteps)
            // binding.taskTime.text = totalSteps.toString()
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }
}